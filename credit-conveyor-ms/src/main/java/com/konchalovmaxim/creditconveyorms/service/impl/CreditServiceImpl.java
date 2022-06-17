package com.konchalovmaxim.creditconveyorms.service.impl;

import com.konchalovmaxim.creditconveyorms.dto.CreditDTO;
import com.konchalovmaxim.creditconveyorms.dto.PaymentScheduleElement;
import com.konchalovmaxim.creditconveyorms.dto.ScoringDataDTO;
import com.konchalovmaxim.creditconveyorms.service.CreditService;
import com.konchalovmaxim.creditconveyorms.service.ScoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.Duration.between;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditServiceImpl implements CreditService {

    private final ScoringService scoringService;

    private static final BigDecimal DAYS_A_YEAR = BigDecimal.valueOf(365);
    private static final BigDecimal HUNDRED_PERCENT = BigDecimal.valueOf(100);

    public CreditDTO createCredit(ScoringDataDTO scoringDataDTO) {

        BigDecimal rate = scoringService.scoring(scoringDataDTO);

        if (rate.compareTo(BigDecimal.ZERO) > 0) {

            CreditDTO creditDTO = new CreditDTO();

            creditDTO.setRate(rate);
            creditDTO.setAmount(scoringDataDTO.getAmount());
            creditDTO.setTerm(scoringDataDTO.getTerm());
            creditDTO.setIsInsuranceEnabled(scoringDataDTO.getIsInsuranceEnabled());
            creditDTO.setIsSalaryClient(scoringDataDTO.getIsSalaryClient());

            creditDTO.setMonthlyPayment(scoringService.getMonthlyPayment(creditDTO.getTerm(), creditDTO.getRate(), creditDTO.getAmount()));

            creditDTO.setPsk(calculatePsk(creditDTO.getMonthlyPayment(), creditDTO.getTerm(), creditDTO.getAmount())
                    .setScale(2, RoundingMode.HALF_UP));

            creditDTO.setPaymentSchedule(getPaymentSchedule(creditDTO));

            return creditDTO;
        }
        return null;
    }

    private BigDecimal calculatePsk(BigDecimal monthlyPayment, Integer term, BigDecimal amount){

        log.debug("Calculating PSK with params: monthlyPayment {}, term {}, amount {}",
                monthlyPayment, term, amount);

        BigDecimal psk = monthlyPayment.multiply(BigDecimal.valueOf(term));

        psk = psk.divide(amount, 10, RoundingMode.HALF_UP).
                subtract(BigDecimal.valueOf(1)).
                divide(BigDecimal.valueOf(term), 10, RoundingMode.HALF_UP).
                multiply(HUNDRED_PERCENT);

        log.debug("PSK: {}", psk);

        return psk;
    }

    private List<PaymentScheduleElement> getPaymentSchedule(CreditDTO creditDTO) {

        List<PaymentScheduleElement> paymentSchedule = new ArrayList<>();

        BigDecimal remainder = creditDTO.getAmount();
        for (int i = 1; i <= creditDTO.getTerm(); i++) {

            LocalDate paymentDate = LocalDate.now().plusMonths(i);

            BigDecimal interestPayment = calculateInterestPayment(remainder, paymentDate, creditDTO.getRate());

            BigDecimal debtPayment = creditDTO.getMonthlyPayment().subtract(interestPayment).setScale(2, RoundingMode.HALF_UP);

            paymentSchedule.add(new PaymentScheduleElement(i, paymentDate, creditDTO.getMonthlyPayment(), interestPayment, debtPayment, remainder));

            remainder = remainder.subtract(debtPayment);
        }

        PaymentScheduleElement lastElement = paymentSchedule.get(paymentSchedule.size() - 1);

        lastElement.setDebtPayment(lastElement.getDebtPayment().add(remainder));
        lastElement.setTotalPayment(lastElement.getTotalPayment().add(remainder));

        return paymentSchedule;
    }

    private BigDecimal calculateInterestPayment(BigDecimal remainder, LocalDate paymentDate, BigDecimal rate){
        long countOfDays = between(paymentDate.minusMonths(1).atStartOfDay(), paymentDate.atStartOfDay()).toDays();

        log.debug("Payment period: {}", countOfDays);

        BigDecimal interestPayment = remainder.
                multiply(rate.divide(HUNDRED_PERCENT, 10, RoundingMode.HALF_UP)).
                multiply(BigDecimal.valueOf(countOfDays).divide(DAYS_A_YEAR, 10, RoundingMode.HALF_UP)).
                setScale(2, RoundingMode.HALF_UP);

        log.debug("Calculate interest payment: {}", interestPayment);

        return interestPayment;
    }
}
