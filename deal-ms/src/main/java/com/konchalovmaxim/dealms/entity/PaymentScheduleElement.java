package com.konchalovmaxim.dealms.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "t_payment_schedule_elements")
public class PaymentScheduleElement {
    @Id
    private Long id;
    private Integer number;
    private LocalDate date;
    private BigDecimal totalPayment;
    private BigDecimal interestPayment;
    private BigDecimal remainingDebt;
}
