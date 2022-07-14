package com.konchalovmaxim.dossier;

import com.konchalovmaxim.dossier.config.KafkaConsumerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableConfigurationProperties({KafkaConsumerProperties.class})
@EnableFeignClients
public class DossierApplication {

    public static void main(String[] args) {
        SpringApplication.run(DossierApplication.class, args);
    }

}
