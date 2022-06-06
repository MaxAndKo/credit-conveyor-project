package com.konchalovmaxim.creditconveyorms.service;

import com.konchalovmaxim.creditconveyorms.dto.*;
import com.konchalovmaxim.creditconveyorms.enums.EmploymentPosition;
import com.konchalovmaxim.creditconveyorms.enums.EmploymentStatus;
import com.konchalovmaxim.creditconveyorms.enums.Gender;
import com.konchalovmaxim.creditconveyorms.enums.MartialStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CreditConveyorServiceTest {

    @Autowired
    CreditConveyorService creditConveyorService;

    @Test
    void getAgeShouldReturnsTwenty() {
        LocalDate birthday = LocalDate.now().minusYears(20);
        assertEquals(20, creditConveyorService.getAge(birthday));
    }

    @Test
    void create4OffersShouldReturnsFourCorrectOffers() {
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

        List<LoanOfferDTO> expected = new ArrayList<>(4);
        expected.add(new LoanOfferDTO(0L, BigDecimal.valueOf(300000),
                BigDecimal.valueOf(360191.34).setScale(2, RoundingMode.HALF_UP),
                18, BigDecimal.valueOf(20010.63).setScale(2, RoundingMode.HALF_UP),
                BigDecimal.valueOf(24), false, false));

        expected.add(new LoanOfferDTO(0L, BigDecimal.valueOf(300000),
                BigDecimal.valueOf(354934.80).setScale(2, RoundingMode.HALF_UP),
                18, BigDecimal.valueOf(19718.60).setScale(2, RoundingMode.HALF_UP),
                BigDecimal.valueOf(22), true, false));

        expected.add(new LoanOfferDTO(0L, BigDecimal.valueOf(300000),
                BigDecimal.valueOf(352322.64).setScale(2, RoundingMode.HALF_UP),
                18, BigDecimal.valueOf(19573.48).setScale(2, RoundingMode.HALF_UP),
                BigDecimal.valueOf(21), false, true));

        expected.add(new LoanOfferDTO(0L, BigDecimal.valueOf(300000),
                BigDecimal.valueOf(347130.90).setScale(2, RoundingMode.HALF_UP),
                18, BigDecimal.valueOf(19285.05).setScale(2, RoundingMode.HALF_UP),
                BigDecimal.valueOf(19), true, true));

        List<LoanOfferDTO> actual = creditConveyorService.createFourOffers(loanApplicationRequestDTO);

        assertEquals(expected, actual);
    }

    @Test
    void createCreditShouldReturnsCorrectCredit() {
                EmploymentDTO employmentDTO = new EmploymentDTO(EmploymentStatus.SELF_EMPLOYED,
                "123456",BigDecimal.valueOf(100000), EmploymentPosition.OWNER,
                15, 12);

        ScoringDataDTO scoringDataDTO = new ScoringDataDTO(BigDecimal.valueOf(300000), 18,
                "Иванов", "Иван", "Иванович", Gender.MALE,
                LocalDate.of(2000,10,10), "1234", "1234",
                LocalDate.of(2020, 12, 9), "УФМС Пенза",
                MartialStatus.MARRIED, 1, employmentDTO, "account",
                true, true);

        CreditDTO creditDTO = creditConveyorService.createCredit(scoringDataDTO);

        assertEquals(BigDecimal.valueOf(16), creditDTO.getRate());
        assertEquals(BigDecimal.valueOf(18856.93), creditDTO.getMonthlyPayment());
        assertEquals(BigDecimal.valueOf(0.73), creditDTO.getPsk());
        



    }

}