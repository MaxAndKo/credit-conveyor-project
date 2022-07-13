package com.konchalovmaxim.dealms.service.Impl;

import com.konchalovmaxim.dealms.config.KafkaTopicProperties;
import com.konchalovmaxim.dealms.dto.EmailMessageDTO;
import com.konchalovmaxim.dealms.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaServiceProducerImpl implements KafkaProducerService {

    private final KafkaTemplate<String, EmailMessageDTO> kafkaTemplate;
    private final KafkaTopicProperties kafkaTopicProperties;

    public void sendFinishRegistration(EmailMessageDTO message) {
        kafkaTemplate.send(kafkaTopicProperties.getFinishRegistration(), message);
    }

    @Override
    public void sendCreateDocuments(EmailMessageDTO message) {
        kafkaTemplate.send(kafkaTopicProperties.getCreateDocuments(), message);
    }

    public void sendSendDocuments(EmailMessageDTO message) {
        kafkaTemplate.send(kafkaTopicProperties.getSendDocuments(), message);
    }

    public void sendSes(EmailMessageDTO message) {
        kafkaTemplate.send(kafkaTopicProperties.getSendSes(), message);
    }

    public void sendCreditIssued(EmailMessageDTO message) {
        kafkaTemplate.send(kafkaTopicProperties.getCreditIssued(), message);
    }

    public void sendApplicationDenied(EmailMessageDTO message) {
        kafkaTemplate.send(kafkaTopicProperties.getApplicationDenied(), message);
    }


}