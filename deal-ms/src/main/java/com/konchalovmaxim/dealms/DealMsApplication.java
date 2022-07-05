package com.konchalovmaxim.dealms;

import com.konchalovmaxim.dealms.config.HttpProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(HttpProperties.class)
public class DealMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DealMsApplication.class, args);
    }

}
