package com.konchalovmaxim.creditconveyorms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditDTO {
    private BigDecimal amount;
    private Integer term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private BigDecimal psk;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;
    private List<PaymentScheduleElement> paymentSchedule;

    @Override
    public String toString() {
        return "CreditDTO{" +
                "amount=" + amount +
                ", term=" + term +
                ", monthlyPayment=" + monthlyPayment +
                ", rate=" + rate +
                ", psk=" + psk +
                ", isInsuranceEnabled=" + isInsuranceEnabled +
                ", isSalaryClient=" + isSalaryClient +
                '}';
    }
}
