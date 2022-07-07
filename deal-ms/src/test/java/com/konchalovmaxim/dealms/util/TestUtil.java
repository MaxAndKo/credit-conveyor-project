package com.konchalovmaxim.dealms.util;

import com.konchalovmaxim.dealms.dto.EmploymentDTO;
import com.konchalovmaxim.dealms.dto.FinishRegistrationRequestDTO;
import com.konchalovmaxim.dealms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.dealms.dto.LoanOfferDTO;
import com.konchalovmaxim.dealms.entity.Client;
import com.konchalovmaxim.dealms.entity.Passport;
import com.konchalovmaxim.dealms.enums.EmploymentPosition;
import com.konchalovmaxim.dealms.enums.EmploymentStatus;
import com.konchalovmaxim.dealms.enums.Gender;
import com.konchalovmaxim.dealms.enums.MartialStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TestUtil {
    public static final Long applicationId = 5L;

    public static LoanApplicationRequestDTO getCorrectLoanApplicationRequestDTO() {
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

    public static Client getClientWithPassport() {
        Client client = new Client();
        Passport passport = new Passport();
        passport.setPassportSeries("4312");
        passport.setPassportNumber("568597");
        client.setPassport(new Passport());
        return client;
    }

    public static LoanOfferDTO getCorrectLoanOfferDTO() {
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

    public static FinishRegistrationRequestDTO getCorrectFinishRegistrationRequestDTO() {
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
}
