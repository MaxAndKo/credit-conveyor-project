package com.konchalovmaxim.dealms.service.Impl;

import com.konchalovmaxim.dealms.dto.FinishRegistrationRequestDTO;
import com.konchalovmaxim.dealms.dto.ScoringDataDTO;
import com.konchalovmaxim.dealms.entity.Application;
import com.konchalovmaxim.dealms.entity.Employment;
import com.konchalovmaxim.dealms.service.ScoringService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ScoringServiceImpl implements ScoringService {

    @Override
    public ScoringDataDTO prepareScoringData(Application application, FinishRegistrationRequestDTO requestDTO) {

        ScoringDataDTO scoringDataDTO = initializeScoringDto(application, requestDTO);

        log.debug("Application before update: {}", application);
        updateApplication(application, requestDTO);
        log.debug("Application after update: {}", application);

        return scoringDataDTO;
    }

    private ScoringDataDTO initializeScoringDto(Application application, FinishRegistrationRequestDTO requestDTO){

        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();

        scoringDataDTO.setAmount(application.getLoanOffer().getRequestedAmount());//from LoanOffer
        scoringDataDTO.setTerm(application.getLoanOffer().getTerm());
        scoringDataDTO.setIsInsuranceEnabled(application.getLoanOffer().getIsInsuranceEnabled());
        scoringDataDTO.setIsSalaryClient(application.getLoanOffer().getIsSalaryClient());

        scoringDataDTO.setFirstName(application.getClient().getFirstName());//fromClient
        scoringDataDTO.setLastName(application.getClient().getLastName());
        scoringDataDTO.setMiddleName(application.getClient().getMiddleName());
        scoringDataDTO.setBirthdate(application.getClient().getBirthdate());
        scoringDataDTO.setPassportSeries(application.getClient().getPassport().getPassportSeries());
        scoringDataDTO.setPassportNumber(application.getClient().getPassport().getPassportNumber());

        scoringDataDTO.setGender(requestDTO.getGender());//from FinishRegistrationRequestDTO
        scoringDataDTO.setPassportIssueDate(requestDTO.getPassportIssueDate());
        scoringDataDTO.setPassportIssueBranch(requestDTO.getPassportIssueBranch());
        scoringDataDTO.setMaritalStatus(requestDTO.getMaritalStatus());
        scoringDataDTO.setDependentAmount(requestDTO.getDependentAmount());
        scoringDataDTO.setEmployment(requestDTO.getEmployment());
        scoringDataDTO.setAccount(requestDTO.getAccount());

        return scoringDataDTO;
    }

    @Transactional
    protected void updateApplication(Application application, FinishRegistrationRequestDTO requestDTO){
        application.getClient().getPassport().setPassportIssueBranch(requestDTO.getPassportIssueBranch());//TODO а если клиент уже был и бд и это не первая его заявка, то нужно ли обновлять эти данные или что с ними делать?
        application.getClient().getPassport().setPassportIssueDate(requestDTO.getPassportIssueDate());
        application.getClient().setMaritalStatus(requestDTO.getMaritalStatus());
        application.getClient().setDependentAmount(requestDTO.getDependentAmount());
        application.getClient().setEmployment(new Employment(requestDTO.getEmployment()));
        application.getClient().setAccount(requestDTO.getAccount());
    }

}
