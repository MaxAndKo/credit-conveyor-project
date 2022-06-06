package com.konchalovmaxim.creditconveyorms.service;

import com.konchalovmaxim.creditconveyorms.bean.RateProperties;
import com.konchalovmaxim.creditconveyorms.dto.*;
import com.konchalovmaxim.creditconveyorms.enums.EmploymentPosition;
import com.konchalovmaxim.creditconveyorms.enums.EmploymentStatus;
import com.konchalovmaxim.creditconveyorms.enums.Gender;
import com.konchalovmaxim.creditconveyorms.enums.MartialStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

import static java.time.Duration.between;

@Service
public class CreditConveyorService {

    private final RateProperties rateProperties;

    public CreditConveyorService(RateProperties rateProperties) {
        this.rateProperties = rateProperties;
    }

    public int getAge(LocalDate birthdate){
        LocalDate currentTime = LocalDate.now();
        Period period = Period.between(birthdate, currentTime);
        return period.getYears();
    }

    private BigDecimal calculateBaseRate(Boolean isSalaryClient, Boolean isInsuranceEnabled){
        BigDecimal rate = rateProperties.STANDARD_RATE;
        if (isInsuranceEnabled){
            rate = rate.add(rateProperties.INSURANCE_ENABLED);
        }

        if (isSalaryClient){
           rate = rate.add(rateProperties.SALARY_CLIENT);
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

    public List<LoanOfferDTO> createFourOffers(LoanApplicationRequestDTO preScoredRequest){
        List<LoanOfferDTO> loanOfferDTOS = new ArrayList<>(4);

        loanOfferDTOS.add(calculateOffer(preScoredRequest, false, false));
        loanOfferDTOS.add(calculateOffer(preScoredRequest, true, false));
        loanOfferDTOS.add(calculateOffer(preScoredRequest, false, true));
        loanOfferDTOS.add(calculateOffer(preScoredRequest, true, true));

        return loanOfferDTOS;
    }

    private Boolean isCreditAvailable(ScoringDataDTO scoringDataDTO){
        if (scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentStatus.UNEMPLOYED)
            return false;
        if (scoringDataDTO.getEmployment().getSalary().multiply(BigDecimal.valueOf(20)).
                compareTo(scoringDataDTO.getAmount()) < 0)
            return false;
        int age = getAge(scoringDataDTO.getBirthdate());
        if  (age < 20 || age > 60)
            return false;
        if (scoringDataDTO.getEmployment().getWorkExperienceTotal() < 12)
            return false;
        if (scoringDataDTO.getEmployment().getWorkExperienceCurrent() < 3)
            return false;

        return true;
    }

    private Optional<BigDecimal> scoring(ScoringDataDTO scoringDataDTO){

        if (isCreditAvailable(scoringDataDTO)){

            BigDecimal rate = calculateBaseRate(scoringDataDTO.getIsSalaryClient(),
                    scoringDataDTO.getIsInsuranceEnabled());

            if (scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentStatus.EMPLOYED)
                rate = rate.add(rateProperties.EMPLOYED_RATE);
            else if (scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentStatus.BUSINESS_OWNER)
                rate = rate.add(rateProperties.BUSINESS_OWNER_RATE);

            if (scoringDataDTO.getEmployment().getPosition() == EmploymentPosition.MID_MANAGER)
                rate = rate.add(rateProperties.MID_MANAGER_RATE);
            else if (scoringDataDTO.getEmployment().getPosition() == EmploymentPosition.TOP_MANAGER)
                rate = rate.add(rateProperties.TOP_MANAGER_RATE);

            if (scoringDataDTO.getMaritalStatus() == MartialStatus.MARRIED)
                rate = rate.add(rateProperties.MARRIED_RATE);
            else if (scoringDataDTO.getMaritalStatus() == MartialStatus.DIVORCED)
                rate = rate.add(rateProperties.DIVORCED_RATE);

            if (scoringDataDTO.getDependentAmount() > 1)
                rate = rate.add(rateProperties.DEPENDENT_AMOUNT_RATE);

            int age = getAge(scoringDataDTO.getBirthdate());

            if (scoringDataDTO.getGender() == Gender.FEMALE && age >= 35 && age < 60 ||
                    scoringDataDTO.getGender() == Gender.MALE  && age >= 30 && age < 55)
                rate = rate.add(rateProperties.MIDDLE_AGE_RATE);
            else if (scoringDataDTO.getGender() == Gender.NON_BINARY)
                rate = rate.add(rateProperties.NON_BINARY_RATE);

            return Optional.of(rate);
        }

        return Optional.empty();
    }

    private BigDecimal getMonthlyPayment(Integer term, BigDecimal rate, BigDecimal amount){
        BigDecimal P = rate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);//деление на 100 - получение процентов, на 12 - определение, какую часть от года состаляет
        BigDecimal pow = BigDecimal.valueOf(1).add(P).pow(term);
        BigDecimal annuitetCoef = P.multiply(pow).divide(pow.subtract(BigDecimal.valueOf(1)), 10, RoundingMode.HALF_UP);
        BigDecimal monthlyPayment = annuitetCoef.multiply(amount);
        return monthlyPayment.setScale(2, RoundingMode.HALF_UP);
    }

    private List<PaymentScheduleElement> getPaymentSchedule(CreditDTO creditDTO){

        List<PaymentScheduleElement> paymentSchedule = new ArrayList<>();

        BigDecimal remainder = creditDTO.getAmount();
        for (int i = 1; i <= creditDTO.getTerm(); i++){

            LocalDate paymentDate = LocalDate.now().plusMonths(i);

            long countOfDays = between(paymentDate.minusMonths(1).atStartOfDay(), paymentDate.atStartOfDay()).toDays();

            BigDecimal interestPayment = remainder.multiply(creditDTO.getRate().
                    divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)).
                    multiply(BigDecimal.valueOf(countOfDays).divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP)).
                    setScale(2, RoundingMode.HALF_UP);

            BigDecimal debtPayment = creditDTO.getMonthlyPayment().subtract(interestPayment).setScale(2, RoundingMode.HALF_UP);

            paymentSchedule.add(new PaymentScheduleElement(i,paymentDate, creditDTO.getMonthlyPayment(), interestPayment, debtPayment, remainder));

            remainder = remainder.subtract(debtPayment);
        }

        PaymentScheduleElement lastElement = paymentSchedule.get(paymentSchedule.size() - 1);

        lastElement.setDebtPayment(lastElement.getDebtPayment().add(remainder));
        lastElement.setTotalPayment(lastElement.getTotalPayment().add(remainder));

        return paymentSchedule;
    }

    public CreditDTO createCredit(ScoringDataDTO scoringDataDTO){

        Optional<BigDecimal> rate = scoring(scoringDataDTO);

    if (rate.isPresent() && rate.get().compareTo(BigDecimal.ZERO) > 0){

        CreditDTO creditDTO = new CreditDTO();

        creditDTO.setRate(rate.get());
        creditDTO.setAmount(scoringDataDTO.getAmount());
        creditDTO.setTerm(scoringDataDTO.getTerm());
        creditDTO.setIsInsuranceEnabled(scoringDataDTO.getIsInsuranceEnabled());
        creditDTO.setIsSalaryClient(scoringDataDTO.getIsSalaryClient());

        creditDTO.setMonthlyPayment(getMonthlyPayment(creditDTO.getTerm(), creditDTO.getRate(), creditDTO.getAmount()));

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
}
