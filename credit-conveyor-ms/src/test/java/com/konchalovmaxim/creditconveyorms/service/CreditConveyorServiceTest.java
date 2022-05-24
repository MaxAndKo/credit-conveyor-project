package com.konchalovmaxim.creditconveyorms.service;

import com.konchalovmaxim.creditconveyorms.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
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
        loanApplicationRequestDTO.setAmount(BigDecimal.valueOf(10000));
        loanApplicationRequestDTO.setTerm(6);
        loanApplicationRequestDTO.setFirstName("Иванов");
        loanApplicationRequestDTO.setLastName("Иван");
        loanApplicationRequestDTO.setMiddleName("Иванович");
        loanApplicationRequestDTO.setEmail("ivanov@ivan.iv");
        loanApplicationRequestDTO.setBirthdate(LocalDate.now().minusYears(20));
        loanApplicationRequestDTO.setPassportSeries("1234");
        loanApplicationRequestDTO.setPassportNumber("123456");

        List<LoanOfferDTO> expected = new ArrayList<>(4);
        expected.add(new LoanOfferDTO(null, BigDecimal.valueOf(10000),
                BigDecimal.valueOf(12000.00).setScale(2, RoundingMode.HALF_UP), 6, BigDecimal.valueOf(1765.23),
                BigDecimal.valueOf(20), false, false));

        expected.add(new LoanOfferDTO(null, BigDecimal.valueOf(10000),
                BigDecimal.valueOf(11800.00).setScale(2, RoundingMode.HALF_UP), 6, BigDecimal.valueOf(1755.25),
                BigDecimal.valueOf(18), true, false));

        expected.add(new LoanOfferDTO(null, BigDecimal.valueOf(10000),
                BigDecimal.valueOf(11700.00).setScale(2, RoundingMode.HALF_UP), 6, BigDecimal.valueOf(1750.27),
                BigDecimal.valueOf(17), false, true));

        expected.add(new LoanOfferDTO(null, BigDecimal.valueOf(10000),
                BigDecimal.valueOf(11500.00).setScale(2, RoundingMode.HALF_UP), 6, BigDecimal.valueOf(1740.34),
                BigDecimal.valueOf(15), true, true));

        List<LoanOfferDTO> actual = creditConveyorService.create4Offers(loanApplicationRequestDTO);

        assertEquals(expected, actual);
    }

//    @Test
//    void scoring() {
//        EmploymentDTO employmentDTO = new EmploymentDTO(EmploymentDTO.EmploymentStatus.САМОЗАНЯТЫЙ,
//                "123456",BigDecimal.valueOf(100000), EmploymentDTO.Position.МЕНЕДЖЕР,
//                15, 12);
//
//        ScoringDataDTO scoringDataDTO = new ScoringDataDTO(BigDecimal.valueOf(300000), 18,
//                "Иванов", "Иван", "Иванович", ScoringDataDTO.Gender.МУЖЧИНА,
//                LocalDate.of(2000,10,10), "1234", "1234",
//                LocalDate.of(2020, 12, 9), "УФМС Пенза",
//                ScoringDataDTO.MartialStatus.В_ОТНОШЕНИЯХ, 1, employmentDTO, "account",
//                true, true);
//
//        BigDecimal rate = creditConveyorService.scoring(scoringDataDTO);
//        Assert.assertEquals( BigDecimal.valueOf(15), rate);
//    }

    @Test
    void createCreditShouldReturnsCorrectCredit() {
                EmploymentDTO employmentDTO = new EmploymentDTO(EmploymentDTO.EmploymentStatus.САМОЗАНЯТЫЙ,
                "123456",BigDecimal.valueOf(100000), EmploymentDTO.Position.МЕНЕДЖЕР,
                15, 12);

        ScoringDataDTO scoringDataDTO = new ScoringDataDTO(BigDecimal.valueOf(300000), 18,
                "Иванов", "Иван", "Иванович", ScoringDataDTO.Gender.МУЖЧИНА,
                LocalDate.of(2000,10,10), "1234", "1234",
                LocalDate.of(2020, 12, 9), "УФМС Пенза",
                ScoringDataDTO.MartialStatus.В_ОТНОШЕНИЯХ, 1, employmentDTO, "account",
                true, true);

        CreditDTO creditDTO = creditConveyorService.createCredit(scoringDataDTO);

        assertEquals(BigDecimal.valueOf(18715.44), creditDTO.getMonthlyPayment());
        assertEquals(BigDecimal.valueOf(15), creditDTO.getRate());
        assertEquals(BigDecimal.valueOf(346877.92), creditDTO.getPsk());
        



    }

}