package com.konchalovmaxim.creditconveyorms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.konchalovmaxim.creditconveyorms.bean")
public class CreditConveyorMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreditConveyorMsApplication.class, args);
    }

}
