package com.konchalovmaxim.dealms;

import com.konchalovmaxim.dealms.config.HttpProperties;
import com.konchalovmaxim.dealms.config.KafkaProducerProperties;
import com.konchalovmaxim.dealms.config.KafkaTopicProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties({HttpProperties.class, KafkaProducerProperties.class, KafkaTopicProperties.class})
public class DealMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DealMsApplication.class, args);
    }

}
