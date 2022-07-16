package com.konchalovmaxim.dealms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka.producer")
@Data
public class KafkaProducerProperties {
    private String bootstrapAddress;
    private String groupId;
}
