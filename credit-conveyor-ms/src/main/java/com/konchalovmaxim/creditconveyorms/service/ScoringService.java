package com.konchalovmaxim.creditconveyorms.service;

import com.konchalovmaxim.creditconveyorms.config.RatePropertiesConfiguration;
import com.konchalovmaxim.creditconveyorms.dto.*;
import com.konchalovmaxim.creditconveyorms.enums.EmploymentPosition;
import com.konchalovmaxim.creditconveyorms.enums.EmploymentStatus;
import com.konchalovmaxim.creditconveyorms.enums.Gender;
import com.konchalovmaxim.creditconveyorms.enums.MartialStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Service
public class ScoringService {

    private final RatePropertiesConfiguration ratePropertiesConfiguration;

    private final OfferService offerservice;

    public ScoringService(RatePropertiesConfiguration ratePropertiesConfiguration, OfferService Offerservice) {
        this.ratePropertiesConfiguration = ratePropertiesConfiguration;
        this.offerservice = Offerservice;
    }

    public int getAge(LocalDate birthdate){
        LocalDate currentTime = LocalDate.now();
        Period period = Period.between(birthdate, currentTime);
        return period.getYears();
    }

    public Optional<BigDecimal> scoring(ScoringDataDTO scoringDataDTO){

        if (isCreditAvailable(scoringDataDTO)){

            BigDecimal rate = offerservice.calculateBaseRate(scoringDataDTO.getIsSalaryClient(),
                    scoringDataDTO.getIsInsuranceEnabled());

            if (scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentStatus.EMPLOYED)
                rate = rate.add(ratePropertiesConfiguration.getEmployed());
            else if (scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentStatus.BUSINESS_OWNER)
                rate = rate.add(ratePropertiesConfiguration.getBusinessOwner());

            if (scoringDataDTO.getEmployment().getPosition() == EmploymentPosition.MID_MANAGER)
                rate = rate.add(ratePropertiesConfiguration.getMidManager());
            else if (scoringDataDTO.getEmployment().getPosition() == EmploymentPosition.TOP_MANAGER)
                rate = rate.add(ratePropertiesConfiguration.getTopManager());

            if (scoringDataDTO.getMaritalStatus() == MartialStatus.MARRIED)
                rate = rate.add(ratePropertiesConfiguration.getMarried());
            else if (scoringDataDTO.getMaritalStatus() == MartialStatus.DIVORCED)
                rate = rate.add(ratePropertiesConfiguration.getDivorced());

            if (scoringDataDTO.getDependentAmount() > 1)
                rate = rate.add(ratePropertiesConfiguration.getDependentAmount());

            int age = getAge(scoringDataDTO.getBirthdate());

            if (scoringDataDTO.getGender() == Gender.FEMALE && age >= 35 && age < 60 ||
                    scoringDataDTO.getGender() == Gender.MALE  && age >= 30 && age < 55)
                rate = rate.add(ratePropertiesConfiguration.getMiddleAge());
            else if (scoringDataDTO.getGender() == Gender.NON_BINARY)
                rate = rate.add(ratePropertiesConfiguration.getNonBinary());

            return Optional.of(rate);
        }

        return Optional.empty();
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

}
