package com.konchalovmaxim.creditconveyorms.service;

import com.konchalovmaxim.creditconveyorms.dto.CreditDTO;
import com.konchalovmaxim.creditconveyorms.dto.PaymentScheduleElement;
import com.konchalovmaxim.creditconveyorms.dto.ScoringDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.Duration.between;

@Service
public class CreditService {

    @Autowired
    private ScoringService scoringService;

    @Autowired
    private OfferService offerService;

    public CreditDTO createCredit(ScoringDataDTO scoringDataDTO) {

        Optional<BigDecimal> rate = scoringService.scoring(scoringDataDTO);

        if (rate.isPresent() && rate.get().compareTo(BigDecimal.ZERO) > 0) {

            CreditDTO creditDTO = new CreditDTO();

            creditDTO.setRate(rate.get());
            creditDTO.setAmount(scoringDataDTO.getAmount());
            creditDTO.setTerm(scoringDataDTO.getTerm());
            creditDTO.setIsInsuranceEnabled(scoringDataDTO.getIsInsuranceEnabled());
            creditDTO.setIsSalaryClient(scoringDataDTO.getIsSalaryClient());

            creditDTO.setMonthlyPayment(offerService.getMonthlyPayment(creditDTO.getTerm(), creditDTO.getRate(), creditDTO.getAmount()));

            BigDecimal psk = creditDTO.getMonthlyPayment().multiply(BigDecimal.valueOf(creditDTO.getTerm()));

            psk = psk.divide(creditDTO.getAmount(), 10, RoundingMode.HALF_UP).
                    subtract(BigDecimal.valueOf(1)).
                    divide(BigDecimal.valueOf(creditDTO.getTerm()), 10, RoundingMode.HALF_UP).
                    multiply(BigDecimal.valueOf(100));

            creditDTO.setPsk(psk.setScale(2, RoundingMode.HALF_UP));

            creditDTO.setPaymentSchedule(getPaymentSchedule(creditDTO));

            return creditDTO;
        }
        return null;
    }

    private List<PaymentScheduleElement> getPaymentSchedule(CreditDTO creditDTO) {

        List<PaymentScheduleElement> paymentSchedule = new ArrayList<>();

        BigDecimal remainder = creditDTO.getAmount();
        for (int i = 1; i <= creditDTO.getTerm(); i++) {

            LocalDate paymentDate = LocalDate.now().plusMonths(i);

            long countOfDays = between(paymentDate.minusMonths(1).atStartOfDay(), paymentDate.atStartOfDay()).toDays();

            BigDecimal interestPayment = remainder.multiply(creditDTO.getRate().
                            divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)).
                    multiply(BigDecimal.valueOf(countOfDays).divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP)).
                    setScale(2, RoundingMode.HALF_UP);

            BigDecimal debtPayment = creditDTO.getMonthlyPayment().subtract(interestPayment).setScale(2, RoundingMode.HALF_UP);

            paymentSchedule.add(new PaymentScheduleElement(i, paymentDate, creditDTO.getMonthlyPayment(), interestPayment, debtPayment, remainder));

            remainder = remainder.subtract(debtPayment);
        }

        PaymentScheduleElement lastElement = paymentSchedule.get(paymentSchedule.size() - 1);

        lastElement.setDebtPayment(lastElement.getDebtPayment().add(remainder));
        lastElement.setTotalPayment(lastElement.getTotalPayment().add(remainder));

        return paymentSchedule;
    }


}
