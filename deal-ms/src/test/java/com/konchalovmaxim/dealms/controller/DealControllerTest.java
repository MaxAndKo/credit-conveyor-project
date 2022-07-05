package com.konchalovmaxim.dealms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konchalovmaxim.dealms.dto.*;
import com.konchalovmaxim.dealms.entity.*;
import com.konchalovmaxim.dealms.enums.*;
import com.konchalovmaxim.dealms.filter.HttpMessageLogFormatter;
import com.konchalovmaxim.dealms.service.ApplicationService;
import com.konchalovmaxim.dealms.service.ClientService;
import com.konchalovmaxim.dealms.service.ScoringService;
import com.konchalovmaxim.dealms.util.FeignServiceUtil;
import feign.FeignException;
import feign.Request;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DealController.class)
@ContextConfiguration(classes = DealControllerTest.TestConf.class)
class DealControllerTest {
    private static final Long applicationId = 5L;
    @MockBean
    private ClientService clientService;
    @MockBean
    private ApplicationService applicationService;
    @MockBean
    private FeignServiceUtil feignServiceUtil;
    @MockBean
    private ScoringService scoringService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void uploadContext() {
        Assertions.assertNotNull(mockMvc);
    }

    @Test
    @SneakyThrows
    public void createApplicationShouldReturnBadRequest() {

        LoanApplicationRequestDTO loanApplicationRequestDTO = new LoanApplicationRequestDTO();

        mockMvc.perform(
                post("/deal/application")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanApplicationRequestDTO))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void createApplicationShouldReturnCorrectListAndSetToApplicationPreapprovalStatus() {

        LoanApplicationRequestDTO loanApplicationRequestDTO = getCorrectLoanApplicationRequestDTO();
        Client client = getClientWithPassport();
        when(clientService.saveOrReturnExists(any())).thenReturn(client);
        Application application = new Application(client);
        application.setId(applicationId);
        when(applicationService.save(any())).thenReturn(application);
        when(feignServiceUtil.getLoanOffers(any())).thenReturn(List.of(new LoanOfferDTO()));

        String result = mockMvc.perform(
                post("/deal/application")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanApplicationRequestDTO))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        List<LoanOfferDTO> expected = new ArrayList<>();
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO();
        loanOfferDTO.setApplicationId(applicationId);
        expected.add(loanOfferDTO);
        Assertions.assertEquals(objectMapper.writeValueAsString(expected), result);// должен вернуть лист с 1 loanOfferDTO, где application id = applicationId
        Assertions.assertEquals(ApplicationStatus.PREAPPROVAL, application.getStatus());
        Assertions.assertNull(application.getStatusHistories());
    }

    @Test
    @SneakyThrows
    public void createApplicationShouldReturnNotAcceptableAndSetToApplicationCc_deniedStatus() {

        LoanApplicationRequestDTO loanApplicationRequestDTO = getCorrectLoanApplicationRequestDTO();
        Client client = getClientWithPassport();
        when(clientService.saveOrReturnExists(any())).thenReturn(client);
        Application application = new Application(client);
        application.setId(5L);
        when(applicationService.save(any())).thenReturn(application);
        Exception exception = new FeignException.FeignClientException(
                500,
                "errorErrorError",
                Request.create("POST", "some_url", new HashMap<>(), null, null),
                null,
                null);
        when(feignServiceUtil.getLoanOffers(any())).thenThrow(exception);

        mockMvc.perform(
                post("/deal/application")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanApplicationRequestDTO))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotAcceptable());

        Assertions.assertEquals(ApplicationStatus.CC_DENIED, application.getStatus());
        Assertions.assertNull(application.getStatusHistories());
    }

    @Test
    @SneakyThrows
    public void acceptOfferShouldReturnBadRequest() {

        LoanOfferDTO loanOfferDTO = new LoanOfferDTO();

        mockMvc.perform(
                put("/deal/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanOfferDTO))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void acceptOfferShouldReturnOkAndSetToApplicationApprovedStatus() {

        LoanOfferDTO loanOfferDTO = getCorrectLoanOfferDTO();
        Application application = new Application();
        application.setId(applicationId);
        when(applicationService.findById(any())).thenReturn(application);

        mockMvc.perform(
                put("/deal/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanOfferDTO))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        Assertions.assertEquals(ApplicationStatus.APPROVED, application.getStatus());
        assertThat(application.getLoanOffer()).usingRecursiveComparison().isEqualTo(new LoanOffer(loanOfferDTO));
    }

    @Test
    @SneakyThrows
    public void acceptOfferShouldReturnBadRequestByIncorrectApplicationId() {

        LoanOfferDTO loanOfferDTO = getCorrectLoanOfferDTO();
        when(applicationService.findById(any())).thenReturn(null);

        mockMvc.perform(
                put("/deal/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanOfferDTO))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

    }

    @Test
    @SneakyThrows
    public void finishCalculationShouldReturnBadRequest() {

        FinishRegistrationRequestDTO finishRegistrationRequestDTO = new FinishRegistrationRequestDTO();

        mockMvc.perform(
                put("/deal/calculate/" + applicationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(finishRegistrationRequestDTO))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void finishCalculationShouldReturnOkAndSetToApplicationCc_approvedStatus() {

        FinishRegistrationRequestDTO requestDTO = getCorrectFinishRegistrationRequestDTO();
        Application application = new Application();
        application.setId(applicationId);
        when(applicationService.findById(any())).thenReturn(application);
        when(scoringService.prepareScoringData(any(), any())).thenReturn(null);
        CreditDTO creditDTO = new CreditDTO();
        creditDTO.setPaymentSchedule(new ArrayList<>());
        creditDTO.setAmount(BigDecimal.valueOf(47457457));
        when(feignServiceUtil.getCredit(any())).thenReturn(creditDTO);

        mockMvc.perform(
                put("/deal/calculate/" + applicationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        Assertions.assertEquals(ApplicationStatus.CC_APPROVED, application.getStatus());
        assertThat(application.getCredit()).usingRecursiveComparison().isEqualTo(new Credit(creditDTO));
    }

    @Test
    @SneakyThrows
    public void finishCalculationShouldReturnBadRequestByIncorrectApplicationId() {

        FinishRegistrationRequestDTO requestDTO = getCorrectFinishRegistrationRequestDTO();
        when(applicationService.findById(applicationId)).thenReturn(new Application());

        mockMvc.perform(
                put("/deal/calculate/" + 4)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

    }

    @Test
    @SneakyThrows
    public void finishCalculationShouldReturnNotAcceptableAndSetToApplicationCc_deniedStatus() {

        FinishRegistrationRequestDTO requestDTO = getCorrectFinishRegistrationRequestDTO();
        Application application = new Application();
        application.setId(applicationId);
        when(applicationService.findById(any())).thenReturn(application);
        when(scoringService.prepareScoringData(any(), any())).thenReturn(null);
        Exception exception = new FeignException.FeignClientException(
                500,
                "errorErrorError",
                Request.create("POST", "some_url", new HashMap<>(), null, null),
                null,
                null);
        when(feignServiceUtil.getCredit(any())).thenThrow(exception);

        mockMvc.perform(
                put("/deal/calculate/" + applicationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotAcceptable());

        Assertions.assertEquals(ApplicationStatus.CC_DENIED, application.getStatus());
    }

    private LoanApplicationRequestDTO getCorrectLoanApplicationRequestDTO() {
        LoanApplicationRequestDTO loanDTO = new LoanApplicationRequestDTO();

        loanDTO.setAmount(BigDecimal.valueOf(300000));
        loanDTO.setFirstName("Ivan");
        loanDTO.setLastName("Ivanov");
        loanDTO.setTerm(6);
        loanDTO.setBirthdate(LocalDate.of(1998, 12, 12));
        loanDTO.setPassportSeries("1234");
        loanDTO.setPassportNumber("567890");
        loanDTO.setEmail("sghsfg@pochta.su");

        return loanDTO;
    }

    private Client getClientWithPassport() {
        Client client = new Client();
        Passport passport = new Passport();
        passport.setPassportSeries("4312");
        passport.setPassportNumber("568597");
        client.setPassport(new Passport());
        return client;
    }

    private LoanOfferDTO getCorrectLoanOfferDTO() {
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO();
        loanOfferDTO.setApplicationId(applicationId);
        loanOfferDTO.setRequestedAmount(BigDecimal.valueOf(10000));
        loanOfferDTO.setTotalAmount(BigDecimal.valueOf(12000));
        loanOfferDTO.setTerm(6);
        loanOfferDTO.setMonthlyPayment(BigDecimal.valueOf(2000));
        loanOfferDTO.setRate(BigDecimal.valueOf(20));
        loanOfferDTO.setIsInsuranceEnabled(false);
        loanOfferDTO.setIsSalaryClient(false);
        return loanOfferDTO;
    }

    private FinishRegistrationRequestDTO getCorrectFinishRegistrationRequestDTO() {
        FinishRegistrationRequestDTO requestDTO = new FinishRegistrationRequestDTO();
        requestDTO.setAccount("some_account");
        requestDTO.setDependentAmount(0);
        requestDTO.setMaritalStatus(MartialStatus.SINGLE);
        requestDTO.setGender(Gender.MALE);
        requestDTO.setPassportIssueBranch("Penza");
        requestDTO.setPassportIssueDate(LocalDate.now().minusYears(25));

        EmploymentDTO employmentDTO = new EmploymentDTO();
        employmentDTO.setEmploymentStatus(EmploymentStatus.SELF_EMPLOYED);
        employmentDTO.setEmployerINN("123456");
        employmentDTO.setSalary(BigDecimal.valueOf(100000));
        employmentDTO.setPosition(EmploymentPosition.OWNER);
        employmentDTO.setWorkExperienceCurrent(12);
        employmentDTO.setWorkExperienceTotal(12);
        requestDTO.setEmployment(employmentDTO);
        return requestDTO;
    }

    @TestConfiguration
    public static class TestConf {
        @Bean
        HttpMessageLogFormatter httpmessageLogFormatter() {
            return new HttpMessageLogFormatter();
        }
    }
}