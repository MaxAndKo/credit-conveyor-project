package com.konchalovmaxim.dealms.entity;


import com.konchalovmaxim.dealms.dto.CreditDTO;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "t_credits")
@NoArgsConstructor
public class Credit {
    @Id
    @GeneratedValue(generator = "credit_id_sequence")
    @SequenceGenerator(
            name = "credit_id_sequence",
            sequenceName = "credit_id_sequence",
            allocationSize = 1)
    private Long id;
    private BigDecimal amount;
    private Integer term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private BigDecimal psk;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "credit_id")
    private List<PaymentScheduleElement> paymentSchedule;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;

    public Credit(CreditDTO creditDTO) {

        this.amount = creditDTO.getAmount();
        this.term = creditDTO.getTerm();
        this.term = creditDTO.getTerm();
        this.monthlyPayment = creditDTO.getMonthlyPayment();
        this.rate = creditDTO.getRate();
        this.psk = creditDTO.getPsk();

        List<PaymentScheduleElement> elements = creditDTO.getPaymentSchedule()
                .stream()
                .map(PaymentScheduleElement::new)
                .collect(Collectors.toList());
        this.paymentSchedule = elements;


        this.isInsuranceEnabled = creditDTO.getIsInsuranceEnabled();
        this.isSalaryClient = creditDTO.getIsSalaryClient();
    }
}
