package com.konchalovmaxim.creditconveyorms.service;

import com.konchalovmaxim.creditconveyorms.dto.ScoringDataDTO;
import com.konchalovmaxim.creditconveyorms.enums.EmploymentStatus;
import com.konchalovmaxim.creditconveyorms.exception.CreditNotAvailableException;
import com.konchalovmaxim.creditconveyorms.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class CreditConveyorScoringServiceTest {

    @Autowired
    ScoringService scoringService;

    @Test
    void getAgeShouldReturnsTwenty() {
        LocalDate birthday = LocalDate.now().minusYears(20);

        assertEquals(20, scoringService.getAge(birthday));
    }

    @Test
    void scoringShouldReturnsSomeRate(){
        assertNotNull(scoringService.scoring(TestUtils.createScoringDataDto()));
    }

    @Test
    void scoringShouldThrowsExceptionByEmploymentStatus(){
        ScoringDataDTO scoringDataDTO = TestUtils.createScoringDataDto();
        scoringDataDTO.getEmployment().setEmploymentStatus(EmploymentStatus.UNEMPLOYED);

        CreditNotAvailableException thrown = Assertions.assertThrows(CreditNotAvailableException.class, () -> {
            scoringService.scoring(scoringDataDTO);
        });

        assertEquals("Заявка не одобрена", thrown.getMessage());
    }

    @Test
    void scoringShouldThrowsExceptionBySalary(){
        ScoringDataDTO scoringDataDTO = TestUtils.createScoringDataDto();
        scoringDataDTO.setAmount(BigDecimal.valueOf(3000));
        scoringDataDTO.getEmployment().setSalary(BigDecimal.TEN);

        CreditNotAvailableException thrown = Assertions.assertThrows(CreditNotAvailableException.class, () -> {
            scoringService.scoring(scoringDataDTO);
        });

        assertEquals("Заявка не одобрена", thrown.getMessage());
    }

    @Test
    void scoringShouldThrowsExceptionByAge(){
        ScoringDataDTO scoringDataDTO = TestUtils.createScoringDataDto();
        scoringDataDTO.setBirthdate(LocalDate.now().minusYears(17));
        scoringDataDTO.setPassportIssueDate(LocalDate.now().minusYears(3));

        CreditNotAvailableException thrown = Assertions.assertThrows(CreditNotAvailableException.class, () -> {
            scoringService.scoring(scoringDataDTO);
        });

        assertEquals("Заявка не одобрена", thrown.getMessage());
    }

    @Test
    void scoringShouldThrowsExceptionByWorkExperienceTotal(){
        ScoringDataDTO scoringDataDTO = TestUtils.createScoringDataDto();
        scoringDataDTO.getEmployment().setWorkExperienceTotal(11);

        CreditNotAvailableException thrown = Assertions.assertThrows(CreditNotAvailableException.class, () -> {
            scoringService.scoring(scoringDataDTO);
        });

        assertEquals("Заявка не одобрена", thrown.getMessage());
    }

    @Test
    void scoringShouldThrowsExceptionByWorkExperienceCurrent(){
        ScoringDataDTO scoringDataDTO = TestUtils.createScoringDataDto();
        scoringDataDTO.getEmployment().setWorkExperienceCurrent(2);

        CreditNotAvailableException thrown = Assertions.assertThrows(CreditNotAvailableException.class, () -> {
            scoringService.scoring(scoringDataDTO);
        });

        assertEquals("Заявка не одобрена", thrown.getMessage());
    }
}
