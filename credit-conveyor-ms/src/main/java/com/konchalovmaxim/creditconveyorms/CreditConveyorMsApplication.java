package com.konchalovmaxim.creditconveyorms;

import com.konchalovmaxim.creditconveyorms.config.HttpProperties;
import com.konchalovmaxim.creditconveyorms.config.RateProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({RateProperties.class, HttpProperties.class})
public class CreditConveyorMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreditConveyorMsApplication.class, args);
    }

}
