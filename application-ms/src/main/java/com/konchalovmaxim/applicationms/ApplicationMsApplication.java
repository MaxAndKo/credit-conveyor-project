package com.konchalovmaxim.applicationms;

import com.konchalovmaxim.applicationms.config.HttpProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(HttpProperties.class)
public class ApplicationMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationMsApplication.class, args);
    }

}
