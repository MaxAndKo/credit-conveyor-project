package com.konchalovmaxim.dealms.service.Impl;

import com.konchalovmaxim.dealms.dto.*;
import com.konchalovmaxim.dealms.entity.Application;
import com.konchalovmaxim.dealms.entity.Client;
import com.konchalovmaxim.dealms.entity.Credit;
import com.konchalovmaxim.dealms.entity.LoanOffer;
import com.konchalovmaxim.dealms.enums.ApplicationStatus;
import com.konchalovmaxim.dealms.enums.ChangeType;
import com.konchalovmaxim.dealms.exception.CreditConveyorResponseException;
import com.konchalovmaxim.dealms.exception.NonexistentApplication;
import com.konchalovmaxim.dealms.service.ApplicationService;
import com.konchalovmaxim.dealms.service.ClientService;
import com.konchalovmaxim.dealms.service.DealService;
import com.konchalovmaxim.dealms.service.ScoringService;
import com.konchalovmaxim.dealms.util.FeignServiceUtil;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealServiceImpl implements DealService {

    private final ClientService clientService;
    private final ApplicationService applicationService;
    private final FeignServiceUtil feignServiceUtil;
    private final ScoringService scoringService;

    @Override
    @Transactional
    public List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO requestDTO) {
        Client client = new Client(requestDTO);

        client = clientService.saveOrReturnExists(client);
        log.debug("Client from DB: {}", client);

        Application application = new Application(client);

        try {
            application.setCreationDate(LocalDate.now());
            application = applicationService.save(application);
            log.debug("Application saved: {}", application);

            List<LoanOfferDTO> loanOfferDTOS = feignServiceUtil.getLoanOffers(requestDTO);
            log.debug("CreditConveyor returns offers: {}", loanOfferDTOS);

            application.setStatus(ApplicationStatus.PREAPPROVAL, ChangeType.AUTOMATIC);


            for (LoanOfferDTO loanOfferDTO : loanOfferDTOS) {
                loanOfferDTO.setApplicationId(application.getId());
            }

            return loanOfferDTOS;
        } catch (FeignException.FeignClientException e) {
            application.setStatus(ApplicationStatus.CC_DENIED, ChangeType.AUTOMATIC);
            log.debug("Application status set on: {}", application.getStatus());

            throw new CreditConveyorResponseException(correctMessage(e.getMessage()));
        }
    }

    @Override
    @Transactional
    public void acceptOffer(LoanOfferDTO loanOfferDTO) {
        Application application = applicationService.findById(loanOfferDTO.getApplicationId());
        log.debug("Found application: {}", application);

        if (application == null) {
            throw new NonexistentApplication("Заявки с таким id не существует");
        } else {
            application.setStatus(ApplicationStatus.APPROVED, ChangeType.AUTOMATIC);
            log.debug("Application status set on: {}", application.getStatus());

            application.setLoanOffer(new LoanOffer(loanOfferDTO));
            log.debug("Application loanOffer set on: {}", application.getLoanOffer());

            applicationService.save(application);
        }
    }

    @Override
    @Transactional
    public void finishCalculation(FinishRegistrationRequestDTO requestDTO, Long applicationId) {
        Application application = applicationService.findById(applicationId);
        log.debug("Found application: {}", application);

        if (application == null) {
            throw new NonexistentApplication("Заявки с таким id не существует");
        } else {
            ScoringDataDTO scoringDataDTO = scoringService.prepareScoringData(application, requestDTO);
            log.debug("Received scoringData: {}", scoringDataDTO);
            try {
                CreditDTO creditDTO = feignServiceUtil.getCredit(scoringDataDTO);
                log.debug("CreditConveyor returns credit: {}", creditDTO);

                application.setCredit(new Credit(creditDTO));
                application.setStatus(ApplicationStatus.CC_APPROVED, ChangeType.AUTOMATIC);
                log.debug("Application status set on: {}", application.getStatus());

            } catch (FeignException.FeignClientException e) {
                application.setStatus(ApplicationStatus.CC_DENIED, ChangeType.AUTOMATIC);
                log.debug("Application status set on: {}", application.getStatus());

                throw new CreditConveyorResponseException(correctMessage(e.getMessage()));
            }
        }
    }

    private String correctMessage(String message) {
        int startOfError = message.indexOf("error") + 8;
        int endOfError = message.length() - 3;
        return message.substring(startOfError, endOfError);
    }
}
