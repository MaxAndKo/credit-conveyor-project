package com.konchalovmaxim.dossier.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka.producer")
@Data
public class KafkaConsumerProperties {
    private String bootstrapAddress;
    private String groupId;
}
