package com.konchalovmaxim.creditconveyorms.bean;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RateProperties {
    public final BigDecimal INSURANCE_ENABLED;
    public final BigDecimal SALARY_CLIENT;
    public final BigDecimal STANDARD_RATE;
    public final BigDecimal INSURANCE_COST;
    public final BigDecimal EMPLOYED_RATE;
    public final BigDecimal BUSINESS_OWNER_RATE;
    public final BigDecimal MID_MANAGER_RATE;
    public final BigDecimal TOP_MANAGER_RATE;
    public final BigDecimal MARRIED_RATE;
    public final BigDecimal DIVORCED_RATE;
    public final BigDecimal DEPENDENT_AMOUNT_RATE;
    public final BigDecimal MIDDLE_AGE_RATE;
    public final BigDecimal NON_BINARY_RATE;

    public RateProperties(@Value("${insurance_enabled}") BigDecimal INSURANCE_ENABLED,
                          @Value("${salaryClient}") BigDecimal SALARY_CLIENT,
                          @Value("${standardRate}") BigDecimal STANDARD_RATE,
                          @Value("${insuranceCost}") BigDecimal INSURANCE_COST,
                          @Value("${employed}") BigDecimal EMPLOYED_RATE,
                          @Value("${businessOwner}") BigDecimal BUSINESS_OWNER_RATE,
                          @Value("${midManager}") BigDecimal MID_MANAGER_RATE,
                          @Value("${topManager}") BigDecimal TOP_MANAGER_RATE,
                          @Value("${married}") BigDecimal MARRIED_RATE,
                          @Value("${divorced}") BigDecimal DIVORCED_RATE,
                          @Value("${dependentAmount}") BigDecimal DEPENDENT_AMOUNT_RATE,
                          @Value("${middleAge}") BigDecimal MIDDLE_AGE_RATE,
                          @Value("${nonBinary}") BigDecimal NON_BINARY_RATE) {
        this.INSURANCE_ENABLED = INSURANCE_ENABLED;
        this.SALARY_CLIENT = SALARY_CLIENT;
        this.STANDARD_RATE = STANDARD_RATE;
        this.INSURANCE_COST = INSURANCE_COST;
        this.EMPLOYED_RATE = EMPLOYED_RATE;
        this.BUSINESS_OWNER_RATE = BUSINESS_OWNER_RATE;
        this.MID_MANAGER_RATE = MID_MANAGER_RATE;
        this.TOP_MANAGER_RATE = TOP_MANAGER_RATE;
        this.MARRIED_RATE = MARRIED_RATE;
        this.DIVORCED_RATE = DIVORCED_RATE;
        this.DEPENDENT_AMOUNT_RATE = DEPENDENT_AMOUNT_RATE;
        this.MIDDLE_AGE_RATE = MIDDLE_AGE_RATE;
        this.NON_BINARY_RATE = NON_BINARY_RATE;
    }
}
