package com.konchalovmaxim.dealms.service;

import com.konchalovmaxim.dealms.dto.CreditDTO;
import com.konchalovmaxim.dealms.dto.FinishRegistrationRequestDTO;
import com.konchalovmaxim.dealms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.dealms.dto.LoanOfferDTO;
import com.konchalovmaxim.dealms.entity.Application;
import com.konchalovmaxim.dealms.entity.Client;
import com.konchalovmaxim.dealms.entity.Credit;
import com.konchalovmaxim.dealms.entity.LoanOffer;
import com.konchalovmaxim.dealms.enums.ApplicationStatus;
import com.konchalovmaxim.dealms.exception.CreditConveyorResponseException;
import com.konchalovmaxim.dealms.exception.NonexistentApplication;
import com.konchalovmaxim.dealms.service.Impl.DealServiceImpl;
import com.konchalovmaxim.dealms.util.FeignServiceUtil;
import feign.FeignException;
import feign.Request;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.konchalovmaxim.dealms.util.TestUtil.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DealServiceTest {
    private final DealService dealService;
    private final ClientService clientService;
    private final ApplicationService applicationService;
    private final FeignServiceUtil feignServiceUtil;
    private final ScoringService scoringService;
    private final KafkaProducerService kafkaProducerService;

    public DealServiceTest() {
        clientService = mock(ClientService.class);
        applicationService = mock(ApplicationService.class);
        feignServiceUtil = mock(FeignServiceUtil.class);
        scoringService = mock(ScoringService.class);
        kafkaProducerService = mock(KafkaProducerService.class);

        dealService = new DealServiceImpl(clientService, applicationService, feignServiceUtil, scoringService, kafkaProducerService);
    }

    @Test
    void uploadContext() {
        assertNotNull(dealService);
    }

    @Test
    @SneakyThrows
    void createApplicationShouldReturnCorrectListAndSetToApplicationPreapprovalStatus() {
        LoanApplicationRequestDTO loanApplicationRequestDTO = getCorrectLoanApplicationRequestDTO();
        Client client = getClientWithPassport();
        when(clientService.saveOrReturnExists(any())).thenReturn(client);
        Application application = new Application(client);
        application.setId(applicationId);
        when(applicationService.save(any())).thenReturn(application);
        when(feignServiceUtil.getLoanOffers(any())).thenReturn(List.of(new LoanOfferDTO()));

        List<LoanOfferDTO> result = dealService.createApplication(loanApplicationRequestDTO);

        List<LoanOfferDTO> expected = new ArrayList<>();
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO();
        loanOfferDTO.setApplicationId(applicationId);
        expected.add(loanOfferDTO);
        Assertions.assertEquals(expected, result);// должен вернуть лист с 1 loanOfferDTO, где application id = applicationId
        Assertions.assertEquals(ApplicationStatus.PREAPPROVAL, application.getStatus());
        Assertions.assertNull(application.getStatusHistories());
    }

    @Test
    @SneakyThrows
    void createApplicationShouldThrowCreditConveyorResponseExceptionAndSetToApplicationCc_deniedStatus() {

        LoanApplicationRequestDTO loanApplicationRequestDTO = getCorrectLoanApplicationRequestDTO();
        Client client = getClientWithPassport();
        when(clientService.saveOrReturnExists(any())).thenReturn(client);
        Application application = new Application(client);
        application.setId(5L);
        when(applicationService.save(any())).thenReturn(application);
        Exception exception = new FeignException.FeignClientException(
                500,
                "error\":\"Error\"}}",
                Request.create("POST", "some_url", new HashMap<>(), null, null),
                null,
                null);
        when(feignServiceUtil.getLoanOffers(any())).thenThrow(exception);

        Throwable throwable = Assertions.assertThrows(CreditConveyorResponseException.class, () -> {
            dealService.createApplication(loanApplicationRequestDTO);
        });

        Assertions.assertEquals("Error", throwable.getMessage());
        Assertions.assertEquals(ApplicationStatus.CC_DENIED, application.getStatus());
        Assertions.assertNull(application.getStatusHistories());
    }

    @Test
    @SneakyThrows
    void acceptOfferShouldSetToApplicationApprovedStatusAndLoanOffer() {

        LoanOfferDTO loanOfferDTO = getCorrectLoanOfferDTO();
        Application application = new Application();
        application.setId(applicationId);
        when(applicationService.findById(any())).thenReturn(application);

        dealService.acceptOffer(loanOfferDTO);

        Assertions.assertEquals(ApplicationStatus.APPROVED, application.getStatus());
        assertThat(application.getLoanOffer()).usingRecursiveComparison().isEqualTo(new LoanOffer(loanOfferDTO));
    }

    @Test
    @SneakyThrows
    void acceptOfferShouldThrowNonexistentApplication() {

        LoanOfferDTO loanOfferDTO = getCorrectLoanOfferDTO();
        when(applicationService.findById(any())).thenReturn(null);

        Throwable throwable = Assertions.assertThrows(NonexistentApplication.class, () -> {
            dealService.acceptOffer(loanOfferDTO);
        });

        Assertions.assertEquals("Заявки с таким id не существует", throwable.getMessage());

    }

    @Test
    @SneakyThrows
    void finishCalculationShouldSetToApplicationCc_approvedStatusAndCredit() {

        FinishRegistrationRequestDTO requestDTO = getCorrectFinishRegistrationRequestDTO();
        Application application = new Application();
        application.setId(applicationId);
        when(applicationService.findById(any())).thenReturn(application);
        when(scoringService.prepareScoringData(any(), any())).thenReturn(null);
        CreditDTO creditDTO = new CreditDTO();
        creditDTO.setPaymentSchedule(new ArrayList<>());
        creditDTO.setAmount(BigDecimal.valueOf(47457457));
        when(feignServiceUtil.getCredit(any())).thenReturn(creditDTO);

        dealService.finishCalculation(requestDTO, applicationId);

        Assertions.assertEquals(ApplicationStatus.CC_APPROVED, application.getStatus());
        assertThat(application.getCredit()).usingRecursiveComparison().isEqualTo(new Credit(creditDTO));
    }

    @Test
    @SneakyThrows
    void finishCalculationShouldThrowNonexistentApplication() {

        FinishRegistrationRequestDTO requestDTO = getCorrectFinishRegistrationRequestDTO();
        when(applicationService.findById(applicationId)).thenReturn(new Application());

        Throwable throwable = Assertions.assertThrows(NonexistentApplication.class, () -> {
            dealService.finishCalculation(requestDTO, 2L);
        });

        Assertions.assertEquals("Заявки с таким id не существует", throwable.getMessage());

    }

    @Test
    @SneakyThrows
    void finishCalculationShouldThrowCreditConveyorResponseExceptionAndSetToApplicationCc_deniedStatus() {

        FinishRegistrationRequestDTO requestDTO = getCorrectFinishRegistrationRequestDTO();
        Application application = new Application();
        application.setId(applicationId);
        when(applicationService.findById(any())).thenReturn(application);
        when(scoringService.prepareScoringData(any(), any())).thenReturn(null);
        Exception exception = new FeignException.FeignClientException(
                500,
                "error\":\"Error\"}}",
                Request.create("POST", "some_url", new HashMap<>(), null, null),
                null,
                null);
        when(feignServiceUtil.getCredit(any())).thenThrow(exception);

//        mockMvc.perform(
//                put("/deal/calculate/" + applicationId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDTO))
//                        .accept(MediaType.APPLICATION_JSON)
//        ).andExpect(status().isNotAcceptable());

        Throwable throwable = Assertions.assertThrows(CreditConveyorResponseException.class, () -> {
            dealService.finishCalculation(requestDTO, applicationId);
        });

        Assertions.assertEquals("Error", throwable.getMessage());


        Assertions.assertEquals(ApplicationStatus.CC_DENIED, application.getStatus());
    }

}
