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

    public void requireFinishRegistration(EmailMessageDTO message) {
        kafkaTemplate.send(kafkaTopicProperties.getFinishRegistration(), message);
    }

    @Override
    public void requireCreateDocuments(EmailMessageDTO message) {
        kafkaTemplate.send(kafkaTopicProperties.getCreateDocuments(), message);
    }

    @Override
    public void requireSendDocuments(EmailMessageDTO message) {
        kafkaTemplate.send(kafkaTopicProperties.getSendDocuments(), message);
    }

    @Override
    public void requireSendSes(EmailMessageDTO message) {
        kafkaTemplate.send(kafkaTopicProperties.getSendSes(), message);
    }

    @Override
    public void sendCreditIssued(EmailMessageDTO message) {
        kafkaTemplate.send(kafkaTopicProperties.getCreditIssued(), message);
    }

    @Override
    public void sendApplicationDenied(EmailMessageDTO message) {
        kafkaTemplate.send(kafkaTopicProperties.getApplicationDenied(), message);
    }


}
