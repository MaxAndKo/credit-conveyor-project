package com.konchalovmaxim.dealms.entity;

import com.konchalovmaxim.dealms.dto.LoanOfferDTO;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "t_loan_offers")
@NoArgsConstructor
@Getter
@Setter
public class LoanOffer {
    @Id
    @GeneratedValue(generator = "loan_offer_id_sequence")
    @SequenceGenerator(name = "loan_offer_id_sequence", sequenceName = "loan_offer_id_sequence", allocationSize = 1)
    private Long id;
    private Long applicationId;
    private BigDecimal requestedAmount;
    private BigDecimal totalAmount;
    private Integer term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;

    public LoanOffer(LoanOfferDTO loanOfferDTO) {
        applicationId = loanOfferDTO.getApplicationId();
        requestedAmount = loanOfferDTO.getRequestedAmount();
        totalAmount = loanOfferDTO.getTotalAmount();
        term = loanOfferDTO.getTerm();
        monthlyPayment = loanOfferDTO.getMonthlyPayment();
        rate = loanOfferDTO.getRate();
        isInsuranceEnabled = loanOfferDTO.getIsInsuranceEnabled();
        isSalaryClient = loanOfferDTO.getIsSalaryClient();
    }
}
