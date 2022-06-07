package com.konchalovmaxim.creditconveyorms.service;

import com.konchalovmaxim.creditconveyorms.bean.RateProperties;
import com.konchalovmaxim.creditconveyorms.dto.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.Duration.between;

@Service
public class OfferService {

    private final RateProperties rateProperties;

    public OfferService(RateProperties rateProperties) {
        this.rateProperties = rateProperties;
    }

    public BigDecimal calculateBaseRate(Boolean isSalaryClient, Boolean isInsuranceEnabled){
        BigDecimal rate = rateProperties.getStandardRate();
        if (isInsuranceEnabled){
            rate = rate.add(rateProperties.getInsuranceEnabled());
        }

        if (isSalaryClient){
            rate = rate.add(rateProperties.getSalaryClient());
        }
        return rate;
    }

    private LoanOfferDTO calculateOffer(LoanApplicationRequestDTO preScoredRequest, Boolean isInsuranceEnabled,
                                        Boolean isSalaryClient){
        BigDecimal rate = calculateBaseRate(isSalaryClient, isInsuranceEnabled);

        BigDecimal monthlyPayment = getMonthlyPayment(preScoredRequest.getTerm(), rate, preScoredRequest.getAmount());

        BigDecimal totalAmount = monthlyPayment.multiply(BigDecimal.valueOf(preScoredRequest.getTerm()));

        return new LoanOfferDTO(0L, preScoredRequest.getAmount(), totalAmount,
                preScoredRequest.getTerm(), monthlyPayment, rate, isInsuranceEnabled, isSalaryClient);
    }

    public BigDecimal getMonthlyPayment(Integer term, BigDecimal rate, BigDecimal amount){
        BigDecimal P = rate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);//деление на 100 - получение процентов, на 12 - определение, какую часть от года состаляет
        BigDecimal pow = BigDecimal.valueOf(1).add(P).pow(term);
        BigDecimal annuitetCoef = P.multiply(pow).divide(pow.subtract(BigDecimal.valueOf(1)), 10, RoundingMode.HALF_UP);
        BigDecimal monthlyPayment = annuitetCoef.multiply(amount);
        return monthlyPayment.setScale(2, RoundingMode.HALF_UP);
    }

    public List<LoanOfferDTO> createFourOffers(LoanApplicationRequestDTO preScoredRequest){
        List<LoanOfferDTO> loanOfferDTOS = new ArrayList<>(4);

        loanOfferDTOS.add(calculateOffer(preScoredRequest, false, false));
        loanOfferDTOS.add(calculateOffer(preScoredRequest, true, false));
        loanOfferDTOS.add(calculateOffer(preScoredRequest, false, true));
        loanOfferDTOS.add(calculateOffer(preScoredRequest, true, true));

        return loanOfferDTOS;
    }
}
