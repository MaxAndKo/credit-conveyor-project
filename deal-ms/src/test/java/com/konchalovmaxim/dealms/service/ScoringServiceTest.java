package com.konchalovmaxim.dealms.service;


import com.konchalovmaxim.dealms.dto.EmploymentDTO;
import com.konchalovmaxim.dealms.dto.FinishRegistrationRequestDTO;
import com.konchalovmaxim.dealms.dto.ScoringDataDTO;
import com.konchalovmaxim.dealms.entity.*;
import com.konchalovmaxim.dealms.enums.EmploymentPosition;
import com.konchalovmaxim.dealms.enums.EmploymentStatus;
import com.konchalovmaxim.dealms.enums.Gender;
import com.konchalovmaxim.dealms.enums.MartialStatus;
import com.konchalovmaxim.dealms.service.Impl.ScoringServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ScoringServiceTest {

    private ScoringService scoringService = new ScoringServiceImpl();

    @Test
    public void scoringServiceShouldReturnCorrectScoringDataAndUpdateApplication() {

        Application application = getApplication();
        FinishRegistrationRequestDTO finishRequest = getFinishRequest();

        ScoringDataDTO scoringDataDTO = scoringService.prepareScoringData(application, finishRequest);

        Assertions.assertEquals(scoringDataDTO, getCorrectScoringDto(application, finishRequest));

        Client expected = getUpdatedApplication(finishRequest).getClient();
        Client actual = application.getClient();

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    private Application getApplication() {
        Application application = new Application();

        Client client = new Client();
        client.setFirstName("Ivan");
        client.setLastName("Ivanov");
        Passport passport = new Passport();
        passport.setPassportNumber("123456");
        passport.setPassportSeries("7890");
        client.setPassport(passport);

        application.setClient(client);

        LoanOffer loanOffer = new LoanOffer();
        loanOffer.setRequestedAmount(BigDecimal.valueOf(300000));
        loanOffer.setTerm(6);
        loanOffer.setIsInsuranceEnabled(true);
        loanOffer.setIsSalaryClient(true);

        application.setLoanOffer(loanOffer);

        return application;
    }

    private Application getUpdatedApplication(FinishRegistrationRequestDTO finishRequest) {
        Application application = getApplication();

        application.getClient().getPassport().setPassportIssueBranch(finishRequest.getPassportIssueBranch());
        application.getClient().getPassport().setPassportIssueDate(finishRequest.getPassportIssueDate());
        application.getClient().setMaritalStatus(finishRequest.getMaritalStatus());
        application.getClient().setDependentAmount(finishRequest.getDependentAmount());
        application.getClient().setEmployment(new Employment(finishRequest.getEmployment()));
        application.getClient().setAccount(finishRequest.getAccount());

        return application;
    }

    private FinishRegistrationRequestDTO getFinishRequest() {
        FinishRegistrationRequestDTO dto = new FinishRegistrationRequestDTO();
        dto.setGender(Gender.MALE);
        dto.setPassportIssueDate(LocalDate.of(1998, 12, 12));
        dto.setPassportIssueBranch("Пенза");
        dto.setMaritalStatus(MartialStatus.SINGLE);
        dto.setDependentAmount(0);

        EmploymentDTO employmentDTO = new EmploymentDTO();
        employmentDTO.setEmploymentStatus(EmploymentStatus.SELF_EMPLOYED);
        employmentDTO.setEmployerINN("123456");
        employmentDTO.setSalary(BigDecimal.valueOf(100000));
        employmentDTO.setPosition(EmploymentPosition.OWNER);
        employmentDTO.setWorkExperienceCurrent(12);
        employmentDTO.setWorkExperienceTotal(15);

        dto.setEmployment(employmentDTO);
        dto.setAccount("some_account");

        return dto;
    }

    private ScoringDataDTO getCorrectScoringDto(Application application, FinishRegistrationRequestDTO finishRequest) {
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

        scoringDataDTO.setGender(finishRequest.getGender());//from FinishRegistrationRequestDTO
        scoringDataDTO.setPassportIssueDate(finishRequest.getPassportIssueDate());
        scoringDataDTO.setPassportIssueBranch(finishRequest.getPassportIssueBranch());
        scoringDataDTO.setMaritalStatus(finishRequest.getMaritalStatus());
        scoringDataDTO.setDependentAmount(finishRequest.getDependentAmount());
        scoringDataDTO.setEmployment(finishRequest.getEmployment());
        scoringDataDTO.setAccount(finishRequest.getAccount());

        return scoringDataDTO;
    }

}
