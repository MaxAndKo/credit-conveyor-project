package com.konchalovmaxim.dossier.service.Impl;

import com.konchalovmaxim.dossier.dto.DocumentDTO;
import com.konchalovmaxim.dossier.service.EmailService;
import com.konchalovmaxim.dossier.util.FeignUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final EmailService emailService;
    private final FeignUtil feignUtil;

    @KafkaListener(topics = "${kafka.topic.finishRegistration}" , groupId = "${kafka.producer.groupId}",  containerFactory = "kafkaListenerContainerFactory")
    public void listenFinishRegistration(String message) {
        log.info("Received Message: {}", message);
        String email = getEmailFromJsonString(message);
        emailService.sendSimpleEmail(email, "Завершите оформление кредита", "\"Ссылка на завершение оформления\"");
    }

    @KafkaListener(topics = "${kafka.topic.createDocuments}" , groupId = "${kafka.producer.groupId}",  containerFactory = "kafkaListenerContainerFactory")
    public void listenCreateDocuments(String message) {
        log.info("Received Message: {}", message);
        String email = getEmailFromJsonString(message);
        emailService.sendSimpleEmail(email, "Подтвердите начало оформления документов", "\"Ссылка на подтверждение оформления документов\"");
    }

    @KafkaListener(topics = "${kafka.topic.sendDocuments}" , groupId = "${kafka.producer.groupId}",  containerFactory = "kafkaListenerContainerFactory")
    public void listenSendDocuments(String message) {
        log.info("Received Message: {}", message);

        Long applicationId = getApplicationIdFromJsonString(message);
        if (applicationId == null){
            throw new RuntimeException("applicationId is null");
        }

        DocumentDTO dto = feignUtil.getDocumentDTO(applicationId);
        log.info("Received documentDTO: {}", dto);

        String email = getEmailFromJsonString(message);

        String document = createDocument(dto);

        emailService.sendSimpleEmail(email, "Ваши документы оформлены", document);
    }

    @KafkaListener(topics = "${kafka.topic.sendSes}" , groupId = "${kafka.producer.groupId}",  containerFactory = "kafkaListenerContainerFactory")
    public void listenSendSes(String message) {
        log.info("Received Message: {}", message);

        Long applicationId = getApplicationIdFromJsonString(message);
        if (applicationId == null){
            throw new RuntimeException("applicationId is null");
        }

        String sesCode = feignUtil.getSesCode(applicationId);
        log.info("Received sesCode: {}", sesCode);

        String email = getEmailFromJsonString(message);

        emailService.sendSimpleEmail(email, "Подпишите ваши документы", "Подпишите ваши документы с помощью данного кода: " + sesCode);
    }

    @KafkaListener(topics = "${kafka.topic.creditIssued}" , groupId = "${kafka.producer.groupId}",  containerFactory = "kafkaListenerContainerFactory")
    public void listenCreditIssued(String message) {
        log.info("Received Message: {}", message);

        Long applicationId = getApplicationIdFromJsonString(message);
        if (applicationId == null){
            throw new RuntimeException("applicationId is null");
        }

        String email = getEmailFromJsonString(message);

        emailService.sendSimpleEmail(email, "Кредит успешно выдан!", "Кредит успешно выдан! Спасибо за то, что выбрали нас");
    }

    @KafkaListener(topics = "${kafka.topic.applicationDenied}" , groupId = "${kafka.producer.groupId}",  containerFactory = "kafkaListenerContainerFactory")
    public void listenApplicationDenied(String message) {
        log.info("Received Message: {}", message);

        Long applicationId = getApplicationIdFromJsonString(message);
        if (applicationId == null){
            throw new RuntimeException("applicationId is null");
        }

        String email = getEmailFromJsonString(message);

        emailService.sendSimpleEmail(email, "Ваш кредит отклонен", "Ваш кредит отклонен.");
    }

    private String getEmailFromJsonString(String json){
        int start = json.indexOf("address") + 10;
        int end = json.indexOf("\",", start);

        String email = json.substring(start, end);
        log.info("Returns address : {}", email);

        return email;
    }
    private Long getApplicationIdFromJsonString(String json){
        int start = json.indexOf("applicationId") + 15;
        int end = json.indexOf("\",", start);

        String stringId = null;
        if (end == -1){
            stringId = json.substring(start, json.length() - 1);
        }
        else {
            stringId = json.substring(start, end);
        }
        return stringId != null ? Long.parseLong(stringId) : null;
    }
    private String createDocument(DocumentDTO dto){
        StringBuilder stringBuilder = new StringBuilder("Уважаемый ");
        stringBuilder.append(dto.getFirstName() + " " + dto.getLastName());
        stringBuilder.append(", документы для вашего кредита суммой ");
        stringBuilder.append(dto.getAmount());
        stringBuilder.append(" оформлены и готовы к вашей подписи. Перейдите по следующей ссылке для их подписания: \"Ссылка для подписания\"");

        return stringBuilder.toString();
    }

}
