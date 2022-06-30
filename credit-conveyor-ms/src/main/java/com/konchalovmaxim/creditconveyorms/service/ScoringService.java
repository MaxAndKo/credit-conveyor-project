package com.konchalovmaxim.creditconveyorms.service;

import com.konchalovmaxim.creditconveyorms.dto.ScoringDataDTO;
import com.konchalovmaxim.creditconveyorms.exception.CreditNotAvailableException;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ScoringService {

    int getAge(LocalDate birthdate);
    BigDecimal getMonthlyPayment(Integer term, BigDecimal rate, BigDecimal amount);
    BigDecimal scoring(ScoringDataDTO scoringDataDTO) throws CreditNotAvailableException;
    BigDecimal calculateBaseRate(boolean isSalaryClient, boolean isInsuranceEnabled);

}
