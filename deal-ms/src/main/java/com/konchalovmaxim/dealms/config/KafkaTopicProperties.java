package com.konchalovmaxim.dealms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka.topic")
@Data
public class KafkaTopicProperties {
    // private List<String> topics;
    private String finishRegistration;
    private String createDocuments;
    private String sendDocuments;
    private String sendSes;
    private String creditIssued;
    private String applicationDenied;
}
