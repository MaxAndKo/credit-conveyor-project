package com.konchalovmaxim.dealms.service.Impl;

import com.konchalovmaxim.dealms.dto.*;
import com.konchalovmaxim.dealms.entity.Application;
import com.konchalovmaxim.dealms.entity.Client;
import com.konchalovmaxim.dealms.entity.Credit;
import com.konchalovmaxim.dealms.entity.LoanOffer;
import com.konchalovmaxim.dealms.enums.ApplicationStatus;
import com.konchalovmaxim.dealms.enums.ChangeType;
import com.konchalovmaxim.dealms.enums.Theme;
import com.konchalovmaxim.dealms.exception.ApplicationException;
import com.konchalovmaxim.dealms.exception.CreditConveyorResponseException;
import com.konchalovmaxim.dealms.service.*;
import com.konchalovmaxim.dealms.util.FeignServiceUtil;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealServiceImpl implements DealService {

    private final ClientService clientService;
    private final ApplicationService applicationService;
    private final FeignServiceUtil feignServiceUtil;
    private final ScoringService scoringService;
    private final KafkaProducerService kafkaProducerService;


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
        Application application = findApplicationOrThrowException(loanOfferDTO.getApplicationId());
        application.setStatus(ApplicationStatus.APPROVED, ChangeType.AUTOMATIC);
        log.debug("Application status set on: {}", application.getStatus());

        application.setLoanOffer(new LoanOffer(loanOfferDTO));
        log.debug("Application loanOffer set on: {}", application.getLoanOffer());

        applicationService.save(application);
        requireFinishRegistration(application);
    }

    @Override
    @Transactional
    public void finishCalculation(FinishRegistrationRequestDTO requestDTO, Long applicationId) {
        Application application = findApplicationOrThrowException(applicationId);

        ScoringDataDTO scoringDataDTO = scoringService.prepareScoringData(application, requestDTO);
        log.debug("Received scoringData: {}", scoringDataDTO);
        try {
            CreditDTO creditDTO = feignServiceUtil.getCredit(scoringDataDTO);
            log.debug("CreditConveyor returns credit: {}", creditDTO);

            application.setCredit(new Credit(creditDTO));
            application.setStatus(ApplicationStatus.CC_APPROVED, ChangeType.AUTOMATIC);
            log.debug("Application status set on: {}", application.getStatus());

            requireCreateDocuments(application);

        } catch (FeignException.FeignClientException e) {
            application.setStatus(ApplicationStatus.CC_DENIED, ChangeType.AUTOMATIC);
            log.debug("Application status set on: {}", application.getStatus());

            throw new CreditConveyorResponseException(correctMessage(e.getMessage()));
        }
    }

    private void requireFinishRegistration(Application application) {
        log.info("Request was sent to complete the registration of the application: {}", application);
        kafkaProducerService.requireFinishRegistration(
                new EmailMessageDTO(
                        application.getClient().getEmail(),
                        Theme.FINISH_REGISTRATION,
                        application.getId()));
    }

    private void requireCreateDocuments(Application application) {
        log.info("Request for create documents was sent. Application: {}", application);
        kafkaProducerService.requireCreateDocuments(
                new EmailMessageDTO(
                        application.getClient().getEmail(),
                        Theme.CREATE_DOCUMENTS,
                        application.getId()));
    }

    @Override
    @Transactional
    public void requireDocumentSend(Long applicationId) {
        Application application = findApplicationOrThrowException(applicationId);

        application.setStatus(ApplicationStatus.PREPARE_DOCUMENTS);
        log.info("Request for send documents was sent. Application: {}", application);
        kafkaProducerService.requireSendDocuments(
                new EmailMessageDTO(
                        application.getClient().getEmail(),
                        Theme.SEND_DOCUMENTS,
                        application.getId()));
    }

    @Override
    @Transactional
    public DocumentDTO getDocument(Long applicationId) {
        Application application = findApplicationOrThrowException(applicationId);
        application.setStatus(ApplicationStatus.DOCUMENT_CREATED);

        DocumentDTO dto = new DocumentDTO(application);
        log.info("Converted DocumentDTO: {}", dto);

        return dto;
    }

    @Override
    @Transactional
    public void requireSes(Long applicationId) {
        Application application = findApplicationOrThrowException(applicationId);
        log.info("Request for send documents was sent. Application: {}", application);
        kafkaProducerService.requireSendSes(
                new EmailMessageDTO(
                        application.getClient().getEmail(),
                        Theme.SEND_SES,
                        application.getId()));
    }

    @Override
    @Transactional
    public String getSes(Long applicationId) {
        Application application = findApplicationOrThrowException(applicationId);
        application.setSesCode(createSesCode());

        log.info("Created Ses: {}", application.getSesCode());

        return application.getSesCode();
    }

    @Override
    @Transactional
    public void documentCode(Long applicationId, String code) {
        Application application = findApplicationOrThrowException(applicationId);
        if (application.getSesCode().equals(code)) {
            application.setStatus(ApplicationStatus.DOCUMENT_SIGNED);
            log.debug("Application status set on: {}", application.getStatus());
            application.setSingDate(LocalDate.now());

            application.setStatus(ApplicationStatus.CREDIT_ISSUED);
            log.debug("Application status set on: {}", application.getStatus());
            kafkaProducerService.sendCreditIssued(
                    new EmailMessageDTO(
                            application.getClient().getEmail(),
                            Theme.CREDIT_ISSUED,
                            application.getId()));
        } else {
            throw new ApplicationException("Неверный код");
        }
    }

    @Override
    public void clientCanceledApplication(Long applicationId) {
        Application application = findApplicationOrThrowException(applicationId);
        application.setStatus(ApplicationStatus.CLIENT_DENIED);
        log.debug("Application status set on: {}", application.getStatus());
        kafkaProducerService.sendApplicationDenied(
                new EmailMessageDTO(
                        application.getClient().getEmail(),
                        Theme.APPLICATION_DENIED,
                        application.getId()
                )
        );
    }

    @Override
    public Application findApplication(Long applicationId) {
        Application application = applicationService.findById(applicationId);
        log.info("Found application: {}", application);
        return application;
    }

    @Override
    public List<Application> findAllApplications() {
        log.info("Finding all applications");
        return applicationService.findAll();
    }

    @Transactional
    Application findApplicationOrThrowException(Long applicationId) throws ApplicationException {
        log.info("Received application id = {}", applicationId);
        Application application = applicationService.findById(applicationId);
        if (application == null) {
            log.info("Application with id = {} is not found", applicationId);
            throw new ApplicationException(String.format("Заявки с id = %d не существует", applicationId));
        } else {
            log.info("Application with id = {} is found: {}", applicationId, application);
            return application;
        }
    }


    private String correctMessage(String message) {
        int startOfError = message.indexOf("error") + 8;
        int endOfError = message.length() - 3;
        return message.substring(startOfError, endOfError);
    }

    private String createSesCode() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }
}
