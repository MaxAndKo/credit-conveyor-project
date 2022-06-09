package com.konchalovmaxim.creditconveyorms.service;

import com.konchalovmaxim.creditconveyorms.dto.ScoringDataDTO;
import com.konchalovmaxim.creditconveyorms.exception.CreditNotAvailableException;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ScoringService {

    public int getAge(LocalDate birthdate);
    public BigDecimal getMonthlyPayment(Integer term, BigDecimal rate, BigDecimal amount);
    public BigDecimal scoring(ScoringDataDTO scoringDataDTO) throws CreditNotAvailableException;
    public BigDecimal calculateBaseRate(Boolean isSalaryClient, Boolean isInsuranceEnabled);
}
