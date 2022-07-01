package com.konchalovmaxim.dealms.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoanOfferDTO {

    @NotNull
    @Min(value = 1)
    private Long applicationId;
    @NotNull
    @Min(10000)
    private BigDecimal requestedAmount;
    @NotNull
    @Min(10000)
    private BigDecimal totalAmount;
    @Min(6)
    @NotNull
    private Integer term;
    @NotNull
    private BigDecimal monthlyPayment;
    @NotNull
    private BigDecimal rate;
    @NotNull
    private Boolean isInsuranceEnabled;
    @NotNull
    private Boolean isSalaryClient;
}


