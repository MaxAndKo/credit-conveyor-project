package com.konchalovmaxim.applicationms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ApplicationMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationMsApplication.class, args);
    }

}
