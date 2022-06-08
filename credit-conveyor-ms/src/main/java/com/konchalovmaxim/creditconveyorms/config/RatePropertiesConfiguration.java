package com.konchalovmaxim.creditconveyorms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "rate")
@PropertySource("classpath:application.properties")
@Data
public class RatePropertiesConfiguration {
    private BigDecimal insuranceCost;
    private BigDecimal standardRate;
    private BigDecimal insuranceEnabled;
    private BigDecimal salaryClient;
    private BigDecimal employed;
    private BigDecimal businessOwner;
    private BigDecimal midManager;
    private BigDecimal topManager;
    private BigDecimal married;
    private BigDecimal divorced;
    private BigDecimal dependentAmount;
    private BigDecimal middleAge;
    private BigDecimal nonBinary;

}
