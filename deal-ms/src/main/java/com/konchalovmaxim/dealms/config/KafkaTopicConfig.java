package com.konchalovmaxim.dealms.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {

    private final KafkaTopicProperties topics;
    //private final KafkaProducerProperties kafkaProducerProperties;
    //TODO variable kafkaProducerProperties might not have been initialized

    @Value("${kafka.producer.bootstrapAddress}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }
        //TODO можно ли это  делать в цикле?
    @Bean
    public NewTopic topicApplicationDenied() {
        return new NewTopic(topics.getApplicationDenied(), 1, (short) 1);
    }
    @Bean
    public NewTopic topicFinishRegistration() {
        return new NewTopic(topics.getFinishRegistration(), 1, (short) 1);
    }
    @Bean
    public NewTopic topicCreateDocuments() {
        return new NewTopic(topics.getCreateDocuments(), 1, (short) 1);
    }
    @Bean
    public NewTopic topicSendDocuments() {
        return new NewTopic(topics.getSendDocuments(), 1, (short) 1);
    }
    @Bean
    public NewTopic topicSendSes() {
        return new NewTopic(topics.getSendSes(), 1, (short) 1);
    }
    @Bean
    public NewTopic topicCreditIssued() {
        return new NewTopic(topics.getCreditIssued(), 1, (short) 1);
    }
}