package com.konchalovmaxim.creditconveyorms.service;

import com.konchalovmaxim.creditconveyorms.config.RatePropertiesConfiguration;
import com.konchalovmaxim.creditconveyorms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.creditconveyorms.dto.LoanOfferDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class OfferService {

    private final RatePropertiesConfiguration ratePropertiesConfiguration;

    public OfferService(RatePropertiesConfiguration ratePropertiesConfiguration) {
        this.ratePropertiesConfiguration = ratePropertiesConfiguration;
    }

    public BigDecimal calculateBaseRate(Boolean isSalaryClient, Boolean isInsuranceEnabled) {
        BigDecimal rate = ratePropertiesConfiguration.getStandardRate();
        if (isInsuranceEnabled) {
            rate = rate.add(ratePropertiesConfiguration.getInsuranceEnabled());
        }

        if (isSalaryClient) {
            rate = rate.add(ratePropertiesConfiguration.getSalaryClient());
        }
        return rate;
    }

    public BigDecimal getMonthlyPayment(Integer term, BigDecimal rate, BigDecimal amount) {
        BigDecimal P = rate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);//деление на 100 - получение процентов, на 12 - определение, какую часть от года состаляет
        BigDecimal pow = BigDecimal.valueOf(1).add(P).pow(term);
        BigDecimal annuitetCoef = P.multiply(pow).divide(pow.subtract(BigDecimal.valueOf(1)), 10, RoundingMode.HALF_UP);
        BigDecimal monthlyPayment = annuitetCoef.multiply(amount);
        return monthlyPayment.setScale(2, RoundingMode.HALF_UP);
    }

    public List<LoanOfferDTO> createFourOffers(LoanApplicationRequestDTO preScoredRequest) {
        List<LoanOfferDTO> loanOfferDTOS = new ArrayList<>(4);

        loanOfferDTOS.add(calculateOffer(preScoredRequest, false, false));
        loanOfferDTOS.add(calculateOffer(preScoredRequest, true, false));
        loanOfferDTOS.add(calculateOffer(preScoredRequest, false, true));
        loanOfferDTOS.add(calculateOffer(preScoredRequest, true, true));

        return loanOfferDTOS;
    }

    private LoanOfferDTO calculateOffer(LoanApplicationRequestDTO preScoredRequest, Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        BigDecimal rate = calculateBaseRate(isSalaryClient, isInsuranceEnabled);

        BigDecimal monthlyPayment = getMonthlyPayment(preScoredRequest.getTerm(), rate, preScoredRequest.getAmount());

        BigDecimal totalAmount = monthlyPayment.multiply(BigDecimal.valueOf(preScoredRequest.getTerm()));

        return new LoanOfferDTO(0L, preScoredRequest.getAmount(), totalAmount, preScoredRequest.getTerm(), monthlyPayment, rate, isInsuranceEnabled, isSalaryClient);
    }

}
