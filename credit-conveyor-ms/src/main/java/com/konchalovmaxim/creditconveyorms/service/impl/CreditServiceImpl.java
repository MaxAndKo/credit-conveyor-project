package com.konchalovmaxim.creditconveyorms.service.impl;

import com.konchalovmaxim.creditconveyorms.dto.CreditDTO;
import com.konchalovmaxim.creditconveyorms.dto.PaymentScheduleElement;
import com.konchalovmaxim.creditconveyorms.dto.ScoringDataDTO;
import com.konchalovmaxim.creditconveyorms.service.CreditService;
import com.konchalovmaxim.creditconveyorms.service.ScoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

            creditDTO.setPaymentSchedule(getPaymentSchedule(creditDTO));

            creditDTO.setPsk(calculatePsk(creditDTO.getPaymentSchedule(), creditDTO.getAmount()));


            return creditDTO;
        }
        return null;
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

    private BigDecimal calculatePsk(List<PaymentScheduleElement> paymentSchedule, BigDecimal amount){

        int paymentsCount = paymentSchedule.size() + 1;

        double basePeriod = 30d;
        double basePeriodsAYear = 365 / basePeriod;

        double[] paymentsWithLoan = getPaymentsWithLoan(paymentSchedule, paymentsCount, amount.doubleValue());

        long[] daysBetweenLoanAndPayment = getDaysBetweenLoanAndPayment(paymentsCount, paymentSchedule);

        double[] termInFractionsOfTheBasePeriod = new double[paymentsCount];
        double[] numberOfFullBasePeriodsSinceIssue = new double[paymentsCount];

        for (int i = 0; i < paymentsCount; i++){
            termInFractionsOfTheBasePeriod[i] = (daysBetweenLoanAndPayment[i] % basePeriod) / basePeriod;
            numberOfFullBasePeriodsSinceIssue[i] = Math.floor(daysBetweenLoanAndPayment[i] / basePeriod);
        }

        double psk = Math.floor(
                getBasePeriodInterestRate(paymentsWithLoan,
                        numberOfFullBasePeriodsSinceIssue,
                        termInFractionsOfTheBasePeriod,
                        paymentSchedule.size()
                ) * basePeriodsAYear * 100 * 1000) / 1000;

        return BigDecimal.valueOf(psk).setScale(2, RoundingMode.HALF_UP);
    }

    private double getBasePeriodInterestRate(double[] payments,
                                             double[] numberOfFullBasePeriodsSinceIssue,
                                             double[] termInFractionsOfTheBasePeriod,
                                             int paymentsCount){
        double basePeriodInterestRate = 0;
        double amountOfPayment = 1;
        double step = 0.00001;

        while (amountOfPayment > 0) {
            amountOfPayment = 0;

            for (int k = 0; k <= paymentsCount; k++) {
                amountOfPayment = amountOfPayment + payments[k] / (
                        (1 + termInFractionsOfTheBasePeriod[k] * basePeriodInterestRate) *
                                Math.pow(1 + basePeriodInterestRate, numberOfFullBasePeriodsSinceIssue[k]));

            }

            basePeriodInterestRate = basePeriodInterestRate + step;
        }

        return basePeriodInterestRate;
    }

    private long[] getDaysBetweenLoanAndPayment(int paymentsCount, List<PaymentScheduleElement> paymentSchedule){

        LocalDate[] dates = new LocalDate[paymentsCount];
        dates[0] = paymentSchedule.get(0).getDate().minusMonths(1);

        for (int i = 1; i < paymentsCount; i++){
            dates[i] = paymentSchedule.get(i - 1).getDate();
        }

        long[] days = new long[paymentsCount];

        for (int i = 0; i < paymentsCount; i++){
            days[i] = between(dates[0].atStartOfDay(), dates[i].atStartOfDay()).toDays();
        }

        return days;
    }

    private double[] getPaymentsWithLoan(
            List<PaymentScheduleElement> paymentSchedule,
            int paymentsCount,
            double amount){
        double[] sum = new double[paymentsCount];
        sum[0] = -amount;

        for (int i = 1; i < paymentsCount; i++){
            sum[i] = paymentSchedule.get(i - 1).getTotalPayment().doubleValue();
        }

        return sum;
    }
}
