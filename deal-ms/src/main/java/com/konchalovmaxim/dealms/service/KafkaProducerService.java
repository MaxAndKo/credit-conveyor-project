package com.konchalovmaxim.dealms.service;

import com.konchalovmaxim.dealms.dto.EmailMessageDTO;

public interface KafkaProducerService {
    void sendCreateDocuments(EmailMessageDTO message);
}
