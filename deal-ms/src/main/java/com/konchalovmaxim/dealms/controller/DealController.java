package com.konchalovmaxim.dealms.controller;

import com.konchalovmaxim.dealms.dto.*;
import com.konchalovmaxim.dealms.entity.*;
import com.konchalovmaxim.dealms.enums.ApplicationStatus;
import com.konchalovmaxim.dealms.enums.ChangeType;
import com.konchalovmaxim.dealms.exception.CreditConveyorResponseException;
import com.konchalovmaxim.dealms.exception.NonexistentApplication;
import com.konchalovmaxim.dealms.service.ApplicationService;
import com.konchalovmaxim.dealms.service.ClientService;
import com.konchalovmaxim.dealms.service.ScoringService;
import com.konchalovmaxim.dealms.util.FeignServiceUtil;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("deal")
@Validated
@RequiredArgsConstructor
public class DealController {

    private final ClientService clientService;
    private final ApplicationService applicationService;
    private final FeignServiceUtil feignServiceUtil;
    private final ScoringService scoringService;

    @PostMapping("/application")//TODO перед самой сдачей проверить, правильно ли расставляются статусы
    @Transactional
    public List<LoanOfferDTO> createApplication(@RequestBody @Valid LoanApplicationRequestDTO requestDTO) {
            Client client = new Client(requestDTO);

            client = clientService.saveOrReturnExists(client);
            Application application = new Application(client);

        try {
            application.setCreationDate(LocalDate.now());
            application = applicationService.save(application);

            List<LoanOfferDTO> loanOfferDTOS = feignServiceUtil.getLoanOffers(requestDTO);

            application.setStatus(ApplicationStatus.PREAPPROVAL, ChangeType.AUTOMATIC);

            for (LoanOfferDTO loanOfferDTO : loanOfferDTOS) {
                loanOfferDTO.setApplicationId(application.getId());
            }

            return loanOfferDTOS;
            }
            catch (FeignException.FeignClientException e){
                application.setStatus(ApplicationStatus.CC_DENIED, ChangeType.AUTOMATIC);
                throw new CreditConveyorResponseException(correctMessage(e.getMessage()));
            }
    }

    @PutMapping("/offer")
    @Transactional
    public void acceptOffer(@RequestBody @Valid LoanOfferDTO loanOfferDTO) {
        Application application = applicationService.findById(loanOfferDTO.getApplicationId());

        if (application == null) {
            throw new NonexistentApplication("Заявки с таким id не существует");
        } else {
            application.setStatus(ApplicationStatus.APPROVED, ChangeType.AUTOMATIC);
            application.setLoanOffer(new LoanOffer(loanOfferDTO));
            applicationService.save(application);
        }
    }

    @PutMapping("/calculate/{applicationId}")
    @Transactional
    public void finishCalculation(
            @RequestBody @Valid FinishRegistrationRequestDTO requestDTO,
            @PathVariable(required = true) Long applicationId) {

        Application application = applicationService.findById(applicationId);

        if (application == null) {
            throw new NonexistentApplication("Заявки с таким id не существует");
        } else {
            ScoringDataDTO scoringDataDTO = scoringService.prepareScoringData(application, requestDTO);

            try{

                CreditDTO creditDTO = feignServiceUtil.getCredit(scoringDataDTO);
                application.setCredit(new Credit(creditDTO));
                application.setStatus(ApplicationStatus.CC_APPROVED, ChangeType.AUTOMATIC);

            } catch (FeignException.FeignClientException e){
                application.setStatus(ApplicationStatus.CC_DENIED, ChangeType.AUTOMATIC);
                throw new CreditConveyorResponseException(correctMessage(e.getMessage())) ;
            }
        }
    }

    private String correctMessage(String message){
        int startOfError = message.indexOf("error") + 8;
        int endOfError = message.length() - 3;
        return message.substring(startOfError, endOfError);
    }
}
