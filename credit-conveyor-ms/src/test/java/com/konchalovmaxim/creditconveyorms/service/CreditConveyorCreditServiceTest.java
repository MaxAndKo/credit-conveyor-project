package com.konchalovmaxim.creditconveyorms.service;

import com.konchalovmaxim.creditconveyorms.dto.CreditDTO;
import com.konchalovmaxim.creditconveyorms.dto.ScoringDataDTO;
import com.konchalovmaxim.creditconveyorms.exception.CreditNotAvailableException;
import com.konchalovmaxim.creditconveyorms.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CreditConveyorCreditServiceTest {

    @Autowired
    CreditService creditService;

    @Test
    void createCreditShouldThrowsExceptionByAge(){
        CreditNotAvailableException thrown = Assertions.assertThrows(CreditNotAvailableException.class, () -> {
            ScoringDataDTO scoringDataDTO = TestUtils.createScoringDataDto();
            scoringDataDTO.setBirthdate(LocalDate.now().minusYears(17));
            scoringDataDTO.setPassportIssueDate(LocalDate.now().minusYears(3));

            creditService.createCredit(scoringDataDTO);
        });
        assertEquals("Заявка не одобрена", thrown.getMessage());
    }

    @Test
    void createCreditShouldReturnsCorrectCredit() {
        CreditDTO creditDTO = creditService.createCredit(TestUtils.createScoringDataDto());

        assertEquals(BigDecimal.valueOf(16), creditDTO.getRate());
        assertEquals(BigDecimal.valueOf(18856.93), creditDTO.getMonthlyPayment());
        assertEquals(BigDecimal.valueOf(16.08), creditDTO.getPsk());
    }
}
