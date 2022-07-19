package com.konchalovmaxim.dossier.service.Impl;

import com.konchalovmaxim.dossier.config.MessageTextProperties;
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
    private final MessageTextProperties messageTextProperties;

    @KafkaListener(topics = "${kafka.topic.finishRegistration}" , groupId = "${kafka.producer.groupId}",  containerFactory = "kafkaListenerContainerFactory")
    public void listenFinishRegistration(String message) {
        log.info("Received Message from finish-registration: {}", message);
        String email = getEmailFromJsonString(message);
        emailService.sendSimpleEmail(
                email,
                messageTextProperties.getFinishRegistrationSubject(),
                messageTextProperties.getFinishRegistrationMessage());
    }

    @KafkaListener(topics = "${kafka.topic.createDocuments}" , groupId = "${kafka.producer.groupId}",  containerFactory = "kafkaListenerContainerFactory")
    public void listenCreateDocuments(String message) {
        log.info("Received Message from createD=-documents: {}", message);
        String email = getEmailFromJsonString(message);
        emailService.sendSimpleEmail(
                email,
                messageTextProperties.getCreateDocumentsSubject(),
                messageTextProperties.getCreateDocumentsMessage());
    }

    @KafkaListener(topics = "${kafka.topic.sendDocuments}" , groupId = "${kafka.producer.groupId}",  containerFactory = "kafkaListenerContainerFactory")
    public void listenSendDocuments(String message) {
        log.info("Received Message from send-documents: {} ", message);

        Long applicationId = getApplicationIdFromJsonString(message);
        if (applicationId == null){
            throw new RuntimeException("applicationId is null");
        }

        DocumentDTO dto = feignUtil.getDocumentDTO(applicationId);
        log.info("Received documentDTO from deal-ms: {}", dto);

        String email = getEmailFromJsonString(message);

        String document = createDocument(dto);

        emailService.sendSimpleEmail(email, messageTextProperties.getSendDocumentsSubject(), document);
    }

    @KafkaListener(topics = "${kafka.topic.sendSes}" , groupId = "${kafka.producer.groupId}",  containerFactory = "kafkaListenerContainerFactory")
    public void listenSendSes(String message) {
        log.info("Received Message from send-ses: {} ", message);

        Long applicationId = getApplicationIdFromJsonString(message);
        if (applicationId == null){
            throw new RuntimeException("applicationId is null");
        }

        String sesCode = feignUtil.getSesCode(applicationId);
        log.info("Received sesCode from deal-ms: {}", sesCode);

        String email = getEmailFromJsonString(message);

        emailService.sendSimpleEmail(
                email,
                messageTextProperties.getSendSesSubject(),
                messageTextProperties.getSendSesMessage() + sesCode);
    }

    @KafkaListener(topics = "${kafka.topic.creditIssued}" , groupId = "${kafka.producer.groupId}",  containerFactory = "kafkaListenerContainerFactory")
    public void listenCreditIssued(String message) {
        log.info("Received Message from credit-issued: {} ", message);

        Long applicationId = getApplicationIdFromJsonString(message);
        if (applicationId == null){
            throw new RuntimeException("applicationId is null");
        }

        String email = getEmailFromJsonString(message);

        emailService.sendSimpleEmail(
                email,
                messageTextProperties.getCreditIssuedSubject(),
                messageTextProperties.getCreditIssuedMessage());
    }

    @KafkaListener(topics = "${kafka.topic.applicationDenied}" , groupId = "${kafka.producer.groupId}",  containerFactory = "kafkaListenerContainerFactory")
    public void listenApplicationDenied(String message) {
        log.info("Received Message from application-denied: {}", message);

        Long applicationId = getApplicationIdFromJsonString(message);
        if (applicationId == null){
            throw new RuntimeException("applicationId is null");
        }

        String email = getEmailFromJsonString(message);

        emailService.sendSimpleEmail(
                email,
                messageTextProperties.getApplicationDeniedSubject(),
                messageTextProperties.getApplicationDeniedMessage());
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
        StringBuilder stringBuilder = new StringBuilder(messageTextProperties.getSendDocumentsMessage());
        stringBuilder.replace(
                stringBuilder.indexOf("$firstName"),
                stringBuilder.indexOf("$firstName") + "$firstName".length(),
                dto.getFirstName());
        stringBuilder.replace(
                stringBuilder.indexOf("$lastName"),
                stringBuilder.indexOf("$lastName") + "$lastName".length(),
                dto.getLastName());
        stringBuilder.replace(
                stringBuilder.indexOf("$loanAmount"),
                stringBuilder.indexOf("$loanAmount") + "$loanAmount".length(),
                dto.getAmount().toString());
        //TODO доделать
        return stringBuilder.toString();
    }

}
