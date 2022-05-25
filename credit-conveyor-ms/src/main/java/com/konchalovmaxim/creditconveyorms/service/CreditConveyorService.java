package com.konchalovmaxim.creditconveyorms.service;

import com.konchalovmaxim.creditconveyorms.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Service
public class CreditConveyorService {

    @Value("${insurance_enabled}")
    private BigDecimal INSURANCE_ENABLED;
    @Value("${salary_client}")
    private  BigDecimal SALARY_CLIENT;
    @Value("${standardRate}")
    private BigDecimal STANDARD_RATE;
    @Value("${insuranceCost}")
    private BigDecimal INSURANCE_COST;

    public int getAge(LocalDate birthdate){
        LocalDate currentTime = LocalDate.now();
        Period period = Period.between(birthdate, currentTime);
        return period.getYears();
    }

    private BigDecimal calculateRateByInsuranceAndSalaryClient(Boolean isSalaryClient,
                                                                      Boolean isInsuranceEnabled,
                                                                      BigDecimal amount, Integer term){
        BigDecimal rate = STANDARD_RATE;
        if (isInsuranceEnabled){
            rate = rate.subtract(INSURANCE_ENABLED);
        }

        if (isSalaryClient){
           rate = rate.subtract(SALARY_CLIENT);
        }
        return rate;
    }

    public List<LoanOfferDTO> create4Offers(LoanApplicationRequestDTO preScoredRequest){
        List<LoanOfferDTO> loanOfferDTOS = new ArrayList<>(4);
        for (int i = 0; i < 4; i++){

            LoanOfferDTO loanOfferDTO = new LoanOfferDTO();
            loanOfferDTO.setRequestedAmount(preScoredRequest.getAmount());
            loanOfferDTO.setTerm(preScoredRequest.getTerm());
            loanOfferDTO.setIsSalaryClient(false);
            loanOfferDTO.setIsInsuranceEnabled(false);

            if (i == 1 || i == 3){
                loanOfferDTO.setIsInsuranceEnabled(true);
            }

            if (i == 2 || i == 3){
                loanOfferDTO.setIsSalaryClient(true);
            }

            loanOfferDTO.setRate(calculateRateByInsuranceAndSalaryClient(loanOfferDTO.getIsSalaryClient(),
                                loanOfferDTO.getIsInsuranceEnabled(),
                                loanOfferDTO.getRequestedAmount(),
                                loanOfferDTO.getTerm()));

            loanOfferDTO.setMonthlyPayment(getMonthlyPayment(loanOfferDTO.getTerm(), loanOfferDTO.getRate(), loanOfferDTO.getRequestedAmount()));

            loanOfferDTO.setTotalAmount(loanOfferDTO.getMonthlyPayment().multiply(BigDecimal.valueOf(loanOfferDTO.getTerm())));

            loanOfferDTOS.add(loanOfferDTO);
        }
        return loanOfferDTOS;
    }

    private BigDecimal scoring(ScoringDataDTO scoringDataDTO){
        if (scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentDTO.EmploymentStatus.БЕЗРАБОТНЫЙ)
            return null;
        if (scoringDataDTO.getEmployment().getSalary().multiply(BigDecimal.valueOf(20)).compareTo(scoringDataDTO.getAmount()) < 0)
            return null;
        Integer age = getAge(scoringDataDTO.getBirthdate());
        if  (age < 20 || age > 60)
            return null;
        if (scoringDataDTO.getEmployment().getWorkExperienceTotal() < 12)
            return null;
        if (scoringDataDTO.getEmployment().getWorkExperienceCurrent() < 3)
            return null;

        BigDecimal rate = calculateRateByInsuranceAndSalaryClient(scoringDataDTO.getIsSalaryClient(),
                scoringDataDTO.getIsInsuranceEnabled(),
                scoringDataDTO.getAmount(),
                scoringDataDTO.getTerm());

        if (scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentDTO.EmploymentStatus.САМОЗАНЯТЫЙ)
            rate.add(BigDecimal.valueOf(1));
        else if (scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentDTO.EmploymentStatus.ВЛАДЕЛЕЦ_БИЗНЕСА)
            rate.add(BigDecimal.valueOf(3));

        if (scoringDataDTO.getEmployment().getPosition() == EmploymentDTO.Position.МЕНЕДЖЕР)
            rate.subtract(BigDecimal.valueOf(2));
        else if (scoringDataDTO.getEmployment().getPosition() == EmploymentDTO.Position.ТОП_МЕНЕДЖЕР)
            rate.subtract(BigDecimal.valueOf(4));

        if (scoringDataDTO.getMaritalStatus() == ScoringDataDTO.MartialStatus.В_ОТНОШЕНИЯХ)
            rate.subtract(BigDecimal.valueOf(3));
        else if (scoringDataDTO.getMaritalStatus() == ScoringDataDTO.MartialStatus.РАЗВЕДЕН)
            rate.add(BigDecimal.valueOf(1));

        if (scoringDataDTO.getDependentAmount() > 1)
            rate.add(BigDecimal.valueOf(1));

        if (scoringDataDTO.getGender() == ScoringDataDTO.Gender.ЖЕНЩИНА && age >= 35 && age < 60 ||
                scoringDataDTO.getGender() == ScoringDataDTO.Gender.МУЖЧИНА  && age >= 30 && age < 55)
            rate.subtract(BigDecimal.valueOf(3));
        else if (scoringDataDTO.getGender() == ScoringDataDTO.Gender.НЕБИНАРНЫЙ)
            rate.add(BigDecimal.valueOf(3));

        return rate;
    }

    private BigDecimal getMonthlyPayment(Integer term, BigDecimal rate, BigDecimal amount){
        BigDecimal P = rate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_DOWN);//деление на 100 - получение процентов, на 12 - определение, какую часть от года состаляет
        BigDecimal pow = BigDecimal.valueOf(1).add(P).pow(term);
        BigDecimal annuitetCoef = P.multiply(pow).divide(pow.subtract(BigDecimal.valueOf(1)), 10, RoundingMode.HALF_UP);
        BigDecimal monthlyPayment = annuitetCoef.multiply(amount);
        return monthlyPayment.setScale(2, RoundingMode.HALF_UP);
    }

    //TODO  районе 100р получается остаток после всех ежемесячных платежей
    private List<PaymentScheduleElement> getPaymentSchedule(CreditDTO creditDTO){

        List<PaymentScheduleElement> paymentSchedule = new ArrayList<>();

        BigDecimal remainder = creditDTO.getAmount();
        for (int i = 1; i <= creditDTO.getTerm(); i++){
            PaymentScheduleElement element = new PaymentScheduleElement();
            element.setNumber(i);
            element.setTotalPayment(creditDTO.getMonthlyPayment());
            element.setDate(LocalDate.now().plusMonths(i));

            Calendar calendar = new GregorianCalendar(element.getDate().getYear(),
                    element.getDate().getMonth().getValue(),
                    element.getDate().getDayOfMonth());

            //TODO следует ли здесь использовать календарь или заменить на что-то лучшее?
            int countOfDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            element.setInterestPayment(remainder.multiply(creditDTO.getRate().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP))
                    .multiply(BigDecimal.valueOf(countOfDays).divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP));
            element.setRemainingDebt(element.getTotalPayment().subtract(element.getInterestPayment()).setScale(2, RoundingMode.HALF_UP));
            remainder = remainder.subtract(element.getRemainingDebt());

            paymentSchedule.add(element);
        }
        return paymentSchedule;
    }

    public CreditDTO createCredit(ScoringDataDTO scoringDataDTO){
    CreditDTO creditDTO = new CreditDTO();
    creditDTO.setRate(scoring(scoringDataDTO));

    if (creditDTO.getRate() != null){
        creditDTO.setAmount(scoringDataDTO.getAmount());
        creditDTO.setTerm(scoringDataDTO.getTerm());
        creditDTO.setIsInsuranceEnabled(scoringDataDTO.getIsInsuranceEnabled());
        creditDTO.setIsSalaryClient(scoringDataDTO.getIsSalaryClient());

        creditDTO.setMonthlyPayment(getMonthlyPayment(creditDTO.getTerm(), creditDTO.getRate(), creditDTO.getAmount()));

        BigDecimal psk = creditDTO.getMonthlyPayment().multiply(BigDecimal.valueOf(creditDTO.getTerm()));
        if (creditDTO.getIsInsuranceEnabled()){
            psk = psk.add(INSURANCE_COST);
        }
        creditDTO.setPsk(psk);

        creditDTO.setPaymentSchedule(getPaymentSchedule(creditDTO));

        return creditDTO;
    }
        return null;
    }
}
