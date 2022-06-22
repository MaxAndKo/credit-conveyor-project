package com.konchalovmaxim.creditconveyorms.service;

import com.konchalovmaxim.creditconveyorms.dto.*;
import com.konchalovmaxim.creditconveyorms.enums.EmploymentPosition;
import com.konchalovmaxim.creditconveyorms.enums.EmploymentStatus;
import com.konchalovmaxim.creditconveyorms.enums.Gender;
import com.konchalovmaxim.creditconveyorms.enums.MartialStatus;
import com.konchalovmaxim.creditconveyorms.exception.CreditNotAvailableException;
import org.junit.jupiter.api.Assertions;
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
class CreditConveyorLogicServiceTest {

    @Autowired
    ScoringService scoringService;
    @Autowired
    OfferService offerService;
    @Autowired
    CreditService creditService;

    @Test
    void getAgeShouldReturnsTwenty() {
        LocalDate birthday = LocalDate.now().minusYears(20);
        assertEquals(20, scoringService.getAge(birthday));
    }

    @Test
    void isCreditAvailableShouldReturnsTrue(){
        assertTrue(scoringService.isCreditAvailable(createScoringDto()));
    }

    @Test
    void isCreditAvailableShouldReturnsFalseByEmploymentStatus(){
        ScoringDataDTO scoringDataDTO = createScoringDto();
        scoringDataDTO.getEmployment().setEmploymentStatus(EmploymentStatus.UNEMPLOYED);
        assertFalse(scoringService.isCreditAvailable(scoringDataDTO));
    }

    @Test
    void isCreditAvailableShouldReturnsFalseBySalary(){
        ScoringDataDTO scoringDataDTO = createScoringDto();
        scoringDataDTO.setAmount(BigDecimal.valueOf(3000));
        scoringDataDTO.getEmployment().setSalary(BigDecimal.TEN);
        assertFalse(scoringService.isCreditAvailable(scoringDataDTO));
    }

    @Test
    void isCreditAvailableShouldReturnsFalseByAge(){
        ScoringDataDTO scoringDataDTO = createScoringDto();
        scoringDataDTO.setBirthdate(LocalDate.now().minusYears(17));
        scoringDataDTO.setPassportIssueDate(LocalDate.now().minusYears(3));
        assertFalse(scoringService.isCreditAvailable(scoringDataDTO));
    }

    @Test
    void isCreditAvailableShouldReturnsFalseByWorkExperienceTotal(){
        ScoringDataDTO scoringDataDTO = createScoringDto();
        scoringDataDTO.getEmployment().setWorkExperienceTotal(11);
        assertFalse(scoringService.isCreditAvailable(scoringDataDTO));
    }

    @Test
    void isCreditAvailableShouldReturnsFalseByWorkExperienceCurrent(){
        ScoringDataDTO scoringDataDTO = createScoringDto();
        scoringDataDTO.getEmployment().setWorkExperienceCurrent(2);
        assertFalse(scoringService.isCreditAvailable(scoringDataDTO));
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

        List<LoanOfferDTO> actual = offerService.createOffers(loanApplicationRequestDTO);

        List<LoanOfferDTO> expected = createExpectedOffers();

        assertEquals(expected, actual);
    }

    private List<LoanOfferDTO> createExpectedOffers(){
        List<LoanOfferDTO> expected = new ArrayList<>(4);
        expected.add(new LoanOfferDTO(0L, BigDecimal.valueOf(300000),
                BigDecimal.valueOf(360191.34),
                18, BigDecimal.valueOf(20010.63),
                BigDecimal.valueOf(24), false, false));

        expected.add(new LoanOfferDTO(0L, BigDecimal.valueOf(300000),
                BigDecimal.valueOf(354934.80).setScale(2, RoundingMode.HALF_UP),
                18, BigDecimal.valueOf(19718.60).setScale(2, RoundingMode.HALF_UP),
                BigDecimal.valueOf(22), true, false));

        expected.add(new LoanOfferDTO(0L, BigDecimal.valueOf(300000),
                BigDecimal.valueOf(352322.64),
                18, BigDecimal.valueOf(19573.48),
                BigDecimal.valueOf(21), false, true));

        expected.add(new LoanOfferDTO(0L, BigDecimal.valueOf(300000),
                BigDecimal.valueOf(347130.90).setScale(2, RoundingMode.HALF_UP),
                18, BigDecimal.valueOf(19285.05),
                BigDecimal.valueOf(19), true, true));

        return expected;
    }

    @Test
    void createCreditShouldThrowsExceptionByAge(){
        CreditNotAvailableException thrown = Assertions.assertThrows(CreditNotAvailableException.class, () -> {
            ScoringDataDTO scoringDataDTO = createScoringDto();
            scoringDataDTO.setBirthdate(LocalDate.now().minusYears(17));
            scoringDataDTO.setPassportIssueDate(LocalDate.now().minusYears(3));

            creditService.createCredit(scoringDataDTO);
        });
        assertEquals("Заявка не одобрена", thrown.getMessage());
    }

    @Test
    void createCreditShouldReturnsCorrectCredit() {
        CreditDTO creditDTO = creditService.createCredit(createScoringDto());

        assertEquals(BigDecimal.valueOf(16), creditDTO.getRate());
        assertEquals(BigDecimal.valueOf(18856.93), creditDTO.getMonthlyPayment());
        assertEquals(BigDecimal.valueOf(0.73), creditDTO.getPsk());
    }

    private ScoringDataDTO createScoringDto(){
        EmploymentDTO employmentDTO = new EmploymentDTO(EmploymentStatus.SELF_EMPLOYED,
                "123456",BigDecimal.valueOf(100000), EmploymentPosition.OWNER,
                15, 12);

        ScoringDataDTO scoringDataDTO = new ScoringDataDTO(BigDecimal.valueOf(300000), 18,
                "Иванов", "Иван", "Иванович", Gender.MALE,
                LocalDate.now().minusYears(20), "1234", "1234",
                LocalDate.now(), "УФМС Пенза",
                MartialStatus.MARRIED, 1, employmentDTO, "account",
                true, true);
        return scoringDataDTO;
    }

}