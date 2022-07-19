package com.konchalovmaxim.dealms.entity;

import com.konchalovmaxim.dealms.dto.PaymentScheduleElementDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "t_payment_schedule_elements")
@NoArgsConstructor
@Getter
public class PaymentScheduleElement {
    @Id
    @GeneratedValue(generator = "payment_id_sequence")
    @SequenceGenerator(
            name = "payment_id_sequence",
            sequenceName = "payment_id_sequence",
            allocationSize = 1)
    private Long id;
    private Integer number;
    private LocalDate date;
    private BigDecimal totalPayment;
    private BigDecimal interestPayment;
    private BigDecimal remainingDebt;

    public PaymentScheduleElement(PaymentScheduleElementDTO dto) {
        this.number = dto.getNumber();
        this.date = dto.getDate();
        this.totalPayment = dto.getTotalPayment();
        this.interestPayment = dto.getInterestPayment();
        this.remainingDebt = dto.getRemainingDebt();
    }
}
