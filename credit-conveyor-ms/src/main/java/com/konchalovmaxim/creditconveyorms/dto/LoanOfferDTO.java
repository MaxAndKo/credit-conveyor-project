package com.konchalovmaxim.creditconveyorms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Properties;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanOfferDTO {

    private Long applicationId;
    private BigDecimal requestedAmount;
    private BigDecimal totalAmount;
    private Integer term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;
}


