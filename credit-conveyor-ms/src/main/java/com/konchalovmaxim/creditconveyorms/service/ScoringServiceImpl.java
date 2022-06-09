package com.konchalovmaxim.creditconveyorms.service;

import com.konchalovmaxim.creditconveyorms.aspect.LoggingAspect;
import com.konchalovmaxim.creditconveyorms.config.RatePropertiesConfiguration;
import com.konchalovmaxim.creditconveyorms.dto.*;
import com.konchalovmaxim.creditconveyorms.enums.EmploymentPosition;
import com.konchalovmaxim.creditconveyorms.enums.EmploymentStatus;
import com.konchalovmaxim.creditconveyorms.enums.Gender;
import com.konchalovmaxim.creditconveyorms.enums.MartialStatus;
import com.konchalovmaxim.creditconveyorms.exception.CreditNotAvailableException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class ScoringServiceImpl implements ScoringService{

    private static final Logger LOG = LoggerFactory.getLogger(ScoringServiceImpl.class);

    private final RatePropertiesConfiguration ratePropertiesConfiguration;

    public int getAge(LocalDate birthdate){
        LocalDate currentTime = LocalDate.now();
        Period period = Period.between(birthdate, currentTime);
        int age = period.getYears();

        LOG.info(String.format("Method getAge return age: %d", age));

        return age;
    }

    public BigDecimal getMonthlyPayment(Integer term, BigDecimal rate, BigDecimal amount) {
        BigDecimal monthlyInterestRate = rate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);//деление на 100 - получение процентов, на 12 - определение, какую часть от года состаляет

        LOG.info(String.format("Method getMonthlyPayment calculate monthlyInterestRate: %f", monthlyInterestRate));

        BigDecimal poweredRate = BigDecimal.ONE.add(monthlyInterestRate).pow(term);
        BigDecimal annuityCoef = monthlyInterestRate.multiply(poweredRate).
                divide(poweredRate.subtract(BigDecimal.valueOf(1)), 10, RoundingMode.HALF_UP);

        LOG.info(String.format("Method getMonthlyPayment calculate annuity coefficient: %f", annuityCoef));

        BigDecimal monthlyPayment = annuityCoef.multiply(amount);

        LOG.info(String.format("Method getMonthlyPayment return monthlyPayment: %f", monthlyPayment));
        return monthlyPayment.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal scoring(ScoringDataDTO scoringDataDTO) throws CreditNotAvailableException {

        if (isCreditAvailable(scoringDataDTO)){

            BigDecimal rate = calculateBaseRate(scoringDataDTO.getIsSalaryClient(),
                    scoringDataDTO.getIsInsuranceEnabled());

            LOG.info(String.format("Method scoring calculate base rate: %f", rate));

            if (scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentStatus.EMPLOYED)
                rate = rate.add(ratePropertiesConfiguration.getEmployed());
            else if (scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentStatus.BUSINESS_OWNER)
                rate = rate.add(ratePropertiesConfiguration.getBusinessOwner());

            LOG.info(String.format("Method scoring calculate rate with employment status: %f", rate));

            if (scoringDataDTO.getEmployment().getPosition() == EmploymentPosition.MID_MANAGER)
                rate = rate.add(ratePropertiesConfiguration.getMidManager());
            else if (scoringDataDTO.getEmployment().getPosition() == EmploymentPosition.TOP_MANAGER)
                rate = rate.add(ratePropertiesConfiguration.getTopManager());

            LOG.info(String.format("Method scoring calculate rate with employment position: %f", rate));

            if (scoringDataDTO.getMaritalStatus() == MartialStatus.MARRIED)
                rate = rate.add(ratePropertiesConfiguration.getMarried());
            else if (scoringDataDTO.getMaritalStatus() == MartialStatus.DIVORCED)
                rate = rate.add(ratePropertiesConfiguration.getDivorced());

            LOG.info(String.format("Method scoring calculate rate with martial status: %f", rate));

            if (scoringDataDTO.getDependentAmount() > 1)
                rate = rate.add(ratePropertiesConfiguration.getDependentAmount());

            LOG.info(String.format("Method scoring calculate rate with dependent amount: %f", rate));

            int age = getAge(scoringDataDTO.getBirthdate());

            if (scoringDataDTO.getGender() == Gender.FEMALE && age >= 35 && age < 60 ||
                    scoringDataDTO.getGender() == Gender.MALE  && age >= 30 && age < 55)
                rate = rate.add(ratePropertiesConfiguration.getMiddleAge());
            else if (scoringDataDTO.getGender() == Gender.NON_BINARY)
                rate = rate.add(ratePropertiesConfiguration.getNonBinary());

            LOG.info(String.format("Method scoring calculate rate with age and gender and return: %f", rate));
            return rate;
        }

        throw new CreditNotAvailableException("Заявка не одобрена");
    }

    public BigDecimal calculateBaseRate(Boolean isSalaryClient, Boolean isInsuranceEnabled) {
        BigDecimal rate = ratePropertiesConfiguration.getStandardRate();
        if (isInsuranceEnabled) {
            rate = rate.add(ratePropertiesConfiguration.getInsuranceEnabled());
        }

        if (isSalaryClient) {
            rate = rate.add(ratePropertiesConfiguration.getSalaryClient());
        }

        LOG.info(String.format("Method calculateBaseRate calculate base rate: %f", rate));

        return rate;
    }

    private Boolean isCreditAvailable(ScoringDataDTO scoringDataDTO){
        if (scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentStatus.UNEMPLOYED) {
            LOG.info("Method isCreditAvailable return false due employment status");
            return false;
        }

        if (scoringDataDTO.getEmployment().getSalary().multiply(BigDecimal.valueOf(20)).
                compareTo(scoringDataDTO.getAmount()) < 0) {
            LOG.info("Method isCreditAvailable return false due salary");
            return false;
        }

        int age = getAge(scoringDataDTO.getBirthdate());
        if  (age < 20 || age > 60) {
            LOG.info("Method isCreditAvailable return false due age");
            return false;
        }

        if (scoringDataDTO.getEmployment().getWorkExperienceTotal() < 12) {
            LOG.info("Method isCreditAvailable return false due total work experience");
            return false;
        }

        if (scoringDataDTO.getEmployment().getWorkExperienceCurrent() < 3) {
            LOG.info("Method isCreditAvailable return false due current work experience");
            return false;
        }

        LOG.info("Method isCreditAvailable return true");
        return true;
    }

}
