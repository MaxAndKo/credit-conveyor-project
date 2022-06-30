package com.konchalovmaxim.creditconveyorms.utils;

import com.konchalovmaxim.creditconveyorms.dto.EmploymentDTO;
import com.konchalovmaxim.creditconveyorms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.creditconveyorms.dto.ScoringDataDTO;
import com.konchalovmaxim.creditconveyorms.enums.EmploymentPosition;
import com.konchalovmaxim.creditconveyorms.enums.EmploymentStatus;
import com.konchalovmaxim.creditconveyorms.enums.Gender;
import com.konchalovmaxim.creditconveyorms.enums.MartialStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public  class TestUtils {

    public static ScoringDataDTO createScoringDataDto(){
        EmploymentDTO employmentDTO = new EmploymentDTO(EmploymentStatus.SELF_EMPLOYED,
                "123456", BigDecimal.valueOf(100000), EmploymentPosition.OWNER,
                15, 12);

        return new ScoringDataDTO(BigDecimal.valueOf(300000), 18,
                "Иванов", "Иван", "Иванович", Gender.MALE,
                LocalDate.now().minusYears(20), "1234", "123456",
                LocalDate.now(), "УФМС Пенза",
                MartialStatus.MARRIED, 1, employmentDTO, "account",
                true, true);
    }

    public static LoanApplicationRequestDTO createLoanApplicationRequestDTO(){
        LoanApplicationRequestDTO loanApplicationRequestDTO = new LoanApplicationRequestDTO();
        loanApplicationRequestDTO.setAmount(BigDecimal.valueOf(300000));
        loanApplicationRequestDTO.setTerm(18);
        loanApplicationRequestDTO.setFirstName("Иванов");
        loanApplicationRequestDTO.setLastName("Иван");
        loanApplicationRequestDTO.setMiddleName("Иванович");
        loanApplicationRequestDTO.setEmail("ivanov@ivan.iv");
        loanApplicationRequestDTO.setBirthdate(LocalDate.now().minusYears(20));
        loanApplicationRequestDTO.setPassportSeries("1234");
        loanApplicationRequestDTO.setPassportNumber("123456");
        return loanApplicationRequestDTO;
    }
}
