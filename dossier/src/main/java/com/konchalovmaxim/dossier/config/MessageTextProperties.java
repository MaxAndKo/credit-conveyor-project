package com.konchalovmaxim.dossier.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mail.message.text")
@Data
public class MessageTextProperties {
    private String finishRegistrationSubject;
    private String finishRegistrationMessage;
    private String createDocumentsSubject;
    private String createDocumentsMessage;
    private String sendDocumentsSubject;
    private String sendDocumentsMessage;
    private String sendSesSubject;
    private String sendSesMessage;
    private String creditIssuedSubject;
    private String creditIssuedMessage;
    private String applicationDeniedSubject;
    private String applicationDeniedMessage;
}
