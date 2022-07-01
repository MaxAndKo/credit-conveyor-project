package com.konchalovmaxim.dealms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DealMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DealMsApplication.class, args);
    }

}
