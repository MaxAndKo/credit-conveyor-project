package com.konchalovmaxim.creditconveyorms.service;

import com.konchalovmaxim.creditconveyorms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.creditconveyorms.dto.LoanOfferDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService{

    private final ScoringService scoringService;

    public List<LoanOfferDTO> createOffers(LoanApplicationRequestDTO preScoredRequest) {
        return List.of(
                calculateOffer(preScoredRequest, false, false),
                calculateOffer(preScoredRequest, true, false),
                calculateOffer(preScoredRequest, false, true),
                calculateOffer(preScoredRequest, true, true)
        );
    }

    private LoanOfferDTO calculateOffer(LoanApplicationRequestDTO preScoredRequest, Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        BigDecimal rate = scoringService.calculateBaseRate(isSalaryClient, isInsuranceEnabled);

        BigDecimal monthlyPayment = scoringService.getMonthlyPayment(preScoredRequest.getTerm(), rate, preScoredRequest.getAmount());

        BigDecimal totalAmount = monthlyPayment.multiply(BigDecimal.valueOf(preScoredRequest.getTerm()));

        return new LoanOfferDTO(0L, preScoredRequest.getAmount(), totalAmount, preScoredRequest.getTerm(), monthlyPayment, rate, isInsuranceEnabled, isSalaryClient);
    }

}
