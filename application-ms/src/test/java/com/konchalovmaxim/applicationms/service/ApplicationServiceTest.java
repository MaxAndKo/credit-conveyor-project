package com.konchalovmaxim.applicationms.service;

import com.konchalovmaxim.applicationms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.applicationms.dto.LoanOfferDTO;
import com.konchalovmaxim.applicationms.exception.UnderageException;
import com.konchalovmaxim.applicationms.utils.FeignUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApplicationServiceTest {
    private final ApplicationService applicationService;
    private final FeignUtil feignUtil;

    public ApplicationServiceTest() {
        feignUtil = mock(FeignUtil.class);
        applicationService = new ApplicationService(feignUtil);
    }

    @Test
    void loadContext(){
        assertNotNull(feignUtil);
        assertNotNull(applicationService);
    }


    @Test
    void createApplicationShouldReturnExpectedList(){
        List<LoanOfferDTO> expected = new ArrayList<>();
        when(feignUtil.getLoanOffers(any())).thenReturn(expected);
        LoanApplicationRequestDTO loan = new LoanApplicationRequestDTO();
        loan.setBirthdate(LocalDate.now().minusYears(18));

        List<LoanOfferDTO> actual = applicationService.createApplication(loan);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void createApplicationShouldThrowUnderageException(){
        List<LoanOfferDTO> expected = new ArrayList<>();
        when(feignUtil.getLoanOffers(any())).thenReturn(expected);
        LoanApplicationRequestDTO loan = new LoanApplicationRequestDTO();
        loan.setBirthdate(LocalDate.now().minusYears(10));

        Throwable throwable = assertThrows(UnderageException.class, ()->{
            applicationService.createApplication(loan);
        });

        assertEquals("Клиенту нет 18 лет", throwable.getMessage());
    }

    @Test
    void createApplicationShouldThrowTestException(){
        when(feignUtil.getLoanOffers(any())).thenThrow(new TestException());
        LoanApplicationRequestDTO loan = new LoanApplicationRequestDTO();
        loan.setBirthdate(LocalDate.now().minusYears(18));

        Throwable throwable = assertThrows(TestException.class, ()->{
            applicationService.createApplication(loan);
        });

        assertEquals(TestException.exceptionMessage, throwable.getMessage());
    }

    @Test
    void acceptOfferShouldExecuteSuccessfully(){
        applicationService.acceptOffer(new LoanOfferDTO());
    }

    @Test
    void acceptOfferShouldThrowTestException(){
        Mockito.doThrow(new TestException()).when(feignUtil).acceptOffer(any());

        Throwable throwable = assertThrows(TestException.class, ()->{
            applicationService.acceptOffer(new LoanOfferDTO());
        });

        assertEquals(TestException.exceptionMessage, throwable.getMessage());
    }

    private static class TestException extends RuntimeException{
        public static final String exceptionMessage = "Тестовая ошибка";

        public TestException() {
            super(exceptionMessage);
        }
    }

}
