package com.konchalovmaxim.dealms.service;

import com.konchalovmaxim.dealms.dto.EmailMessageDTO;

public interface KafkaProducerService {
    void requireFinishRegistration(EmailMessageDTO message);
    void requireCreateDocuments(EmailMessageDTO message);
    void requireSendDocuments(EmailMessageDTO message);

    void requireSendSes(EmailMessageDTO message);

    void sendCreditIssued(EmailMessageDTO message);

    void sendApplicationDenied(EmailMessageDTO message);
}
