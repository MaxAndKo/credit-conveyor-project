package com.konchalovmaxim.creditconveyorms;

import com.konchalovmaxim.creditconveyorms.config.RatePropertiesConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RatePropertiesConfiguration.class)
public class CreditConveyorMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreditConveyorMsApplication.class, args);
    }

}
