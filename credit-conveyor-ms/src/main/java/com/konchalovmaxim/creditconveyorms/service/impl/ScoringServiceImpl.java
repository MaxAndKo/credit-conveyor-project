package com.konchalovmaxim.creditconveyorms.service.impl;

import com.konchalovmaxim.creditconveyorms.config.RateProperties;
import com.konchalovmaxim.creditconveyorms.dto.ScoringDataDTO;
import com.konchalovmaxim.creditconveyorms.enums.EmploymentPosition;
import com.konchalovmaxim.creditconveyorms.enums.EmploymentStatus;
import com.konchalovmaxim.creditconveyorms.enums.Gender;
import com.konchalovmaxim.creditconveyorms.enums.MartialStatus;
import com.konchalovmaxim.creditconveyorms.exception.CreditNotAvailableException;
import com.konchalovmaxim.creditconveyorms.service.ScoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoringServiceImpl implements ScoringService {

    private final RateProperties rateProperties;

    public int getAge(LocalDate birthdate) {
        LocalDate currentTime = LocalDate.now();
        Period period = Period.between(birthdate, currentTime);
        int age = period.getYears();

        log.debug("Method getAge return age: {}", age);

        return age;
    }

    public BigDecimal getMonthlyPayment(Integer term, BigDecimal rate, BigDecimal amount) {
        BigDecimal monthlyInterestRate = rate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);//деление на 100 - получение процентов, на 12 - определение, какую часть от года состаляет

        log.debug("Calculate monthlyInterestRate: {}", monthlyInterestRate);

        BigDecimal poweredRate = BigDecimal.ONE.add(monthlyInterestRate).pow(term);
        BigDecimal annuityCoef = monthlyInterestRate.multiply(poweredRate).
                divide(poweredRate.subtract(BigDecimal.ONE), 10, RoundingMode.HALF_UP);

        log.debug("Calculate annuity coefficient: {}", annuityCoef);

        BigDecimal monthlyPayment = annuityCoef.multiply(amount);

        log.debug("getMonthlyPayment return monthlyPayment: {}", monthlyPayment);
        return monthlyPayment.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal scoring(ScoringDataDTO scoringDataDTO) throws CreditNotAvailableException {

        if (isCreditAvailable(scoringDataDTO)) {

            BigDecimal rate = calculateBaseRate(scoringDataDTO.getIsSalaryClient(),
                    scoringDataDTO.getIsInsuranceEnabled());

            if (scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentStatus.EMPLOYED)
                rate = rate.add(rateProperties.getEmployed());
            else if (scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentStatus.BUSINESS_OWNER)
                rate = rate.add(rateProperties.getBusinessOwner());

            log.debug("Calculate rate with employment status: {}", rate);

            if (scoringDataDTO.getEmployment().getPosition() == EmploymentPosition.MID_MANAGER)
                rate = rate.add(rateProperties.getMidManager());
            else if (scoringDataDTO.getEmployment().getPosition() == EmploymentPosition.TOP_MANAGER)
                rate = rate.add(rateProperties.getTopManager());

            log.debug("Calculate rate with employment position: {}", rate);

            if (scoringDataDTO.getMaritalStatus() == MartialStatus.MARRIED)
                rate = rate.add(rateProperties.getMarried());
            else if (scoringDataDTO.getMaritalStatus() == MartialStatus.DIVORCED)
                rate = rate.add(rateProperties.getDivorced());

            log.debug("Calculate rate with martial status: {}", rate);

            if (scoringDataDTO.getDependentAmount() > 1)
                rate = rate.add(rateProperties.getDependentAmount());

            log.debug("Calculate rate with dependent amount: {}", rate);

            int age = getAge(scoringDataDTO.getBirthdate());

            if (scoringDataDTO.getGender() == Gender.FEMALE && age >= 35 && age < 60 ||
                    scoringDataDTO.getGender() == Gender.MALE && age >= 30 && age < 55)
                rate = rate.add(rateProperties.getMiddleAge());
            else if (scoringDataDTO.getGender() == Gender.NON_BINARY)
                rate = rate.add(rateProperties.getNonBinary());

            log.debug("Calculate rate with age and gender and return: {}", rate);
            return rate;
        }

        throw new CreditNotAvailableException("Заявка не одобрена");
    }

    public BigDecimal calculateBaseRate(boolean isSalaryClient, boolean isInsuranceEnabled) {
        BigDecimal rate = rateProperties.getStandardRate();
        if (isInsuranceEnabled) {
            rate = rate.add(rateProperties.getInsuranceEnabled());
        }

        if (isSalaryClient) {
            rate = rate.add(rateProperties.getSalaryClient());
        }

        log.debug("Calculate base rate: {}", rate);

        return rate;
    }

    private boolean isCreditAvailable(ScoringDataDTO scoringDataDTO) {
        if (scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentStatus.UNEMPLOYED) {
            log.debug("IsCreditAvailable return false due employment status");
            return false;
        }

        if (scoringDataDTO.getEmployment().getSalary().multiply(BigDecimal.valueOf(20)).
                compareTo(scoringDataDTO.getAmount()) < 0) {
            log.debug("IsCreditAvailable return false due salary");
            return false;
        }

        int age = getAge(scoringDataDTO.getBirthdate());
        if (age < 20 || age > 60) {
            log.debug("IsCreditAvailable return false due age");
            return false;
        }

        if (scoringDataDTO.getEmployment().getWorkExperienceTotal() < 12) {
            log.debug("IsCreditAvailable return false due total work experience");
            return false;
        }

        if (scoringDataDTO.getEmployment().getWorkExperienceCurrent() < 3) {
            log.debug("IsCreditAvailable return false due current work experience");
            return false;
        }

        return true;
    }

}
