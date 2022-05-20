package com.konchalovmaxim.creditconveyorms.utils;

import com.konchalovmaxim.creditconveyorms.dto.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public abstract class creditConveyorUtils {

    private static final int INSURANCE_ENABLED = 2;
    private static final int SALARY_CLIENT = 3;

    public static BigDecimal getStandardRate(){
        File file = new File("credit-conveyor-ms.properties");
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();//TODO добавить логгирование сюда
        }
        return new BigDecimal(properties.getProperty("standardRate"));
    }

    public static int getAge(LocalDate birthdate){
        LocalDate currentTime = LocalDate.now();
        Period period = Period.between(birthdate, currentTime);
        return period.getYears();
    }

    private static BigDecimal calculateRate(Boolean isSalaryClient, Boolean isInsuranceEnabled, BigDecimal amount, Integer term){
        BigDecimal rate = getStandardRate();
        if (isInsuranceEnabled){
            rate = rate.subtract(BigDecimal.valueOf(INSURANCE_ENABLED));
        }

        if (isSalaryClient){
           rate = rate.subtract(BigDecimal.valueOf(SALARY_CLIENT));
        }

        rate = rate.add(BigDecimal.valueOf((0.5 * (term / 6))));//накидывает 0.5% за каждые 6 месяцев срока

        BigDecimal rateDowngradeByAmount = amount.divide(BigDecimal.valueOf(300000), 0, RoundingMode.FLOOR).multiply(BigDecimal.valueOf(0.5));
        rate = rate.subtract(rateDowngradeByAmount);//скидывает 0.5% за каждые 300.000 суммы

        return rate;
    }

    public static List<LoanOfferDTO> create4Offers(LoanApplicationRequestDTO preScoredRequest){
        List<LoanOfferDTO> loanOfferDTOS = new ArrayList<>(4);
        for (int i = 0; i < 4; i++){

            LoanOfferDTO loanOfferDTO = new LoanOfferDTO();
            loanOfferDTO.setRequestedAmount(preScoredRequest.getAmount());
            loanOfferDTO.setTerm(preScoredRequest.getTerm());

            if (i == 1 || i == 3){
                loanOfferDTO.setIsInsuranceEnabled(true);
            }

            if (i == 2 || i == 3){
                loanOfferDTO.setIsSalaryClient(true);
            }

            loanOfferDTO.setRate(calculateRate(loanOfferDTO.getIsSalaryClient(),
                                loanOfferDTO.getIsInsuranceEnabled(),
                                loanOfferDTO.getRequestedAmount(),
                                loanOfferDTO.getTerm()));

            loanOfferDTO.setTotalAmount(loanOfferDTO.getRequestedAmount().add(loanOfferDTO.getRequestedAmount().
                                                                            multiply(loanOfferDTO.getRate().
                                                                                    divide(new BigDecimal(100), 2, RoundingMode.HALF_DOWN))));

            loanOfferDTO.setMonthlyPayment(loanOfferDTO.getTotalAmount().divide(new BigDecimal(loanOfferDTO.getTerm()), 2, RoundingMode.HALF_DOWN));

            loanOfferDTOS.add(loanOfferDTO);
        }
        return loanOfferDTOS;
    }

    public static BigDecimal scoring(ScoringDataDTO scoringDataDTO){
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

        BigDecimal rate = calculateRate(scoringDataDTO.getIsSalaryClient(),
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

    public static CreditDTO createCredit(ScoringDataDTO scoringDataDTO){
    CreditDTO creditDTO = new CreditDTO();
    creditDTO.setAmount(scoringDataDTO.getAmount());
    creditDTO.setTerm(scoringDataDTO.getTerm());
    creditDTO.setIsInsuranceEnabled(scoringDataDTO.getIsInsuranceEnabled());
    creditDTO.setIsSalaryClient(scoringDataDTO.getIsSalaryClient());


    creditDTO.setRate(scoring(scoringDataDTO));

    if (creditDTO.getRate() != null){
        BigDecimal P = creditDTO.getRate().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_DOWN);

        BigDecimal monthlyPayment = creditDTO.getAmount().multiply(P.add(P.divide(P.add(BigDecimal.valueOf(1)).pow(creditDTO.getTerm()).subtract(BigDecimal.valueOf(1)), 10, RoundingMode.HALF_UP)));

        creditDTO.setMonthlyPayment(monthlyPayment.setScale(2, RoundingMode.HALF_UP));

        //TODO добавить расчет ежемесячных платежей

        return creditDTO;
    }

        return null;
    }
}
