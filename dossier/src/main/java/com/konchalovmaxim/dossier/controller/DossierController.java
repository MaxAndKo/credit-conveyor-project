package com.konchalovmaxim.dossier.controller;

import com.konchalovmaxim.dossier.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DossierController {

    private final EmailService emailService;

    @GetMapping("email/{userEmail}")
    public @ResponseBody ResponseEntity sendSimpleEmail(@PathVariable("userEmail") String email) {
        try {
            emailService.sendSimpleEmail(email, "Welcome", "This is a welcome email for your!!");
        } catch (MailException mailException) {
            log.error("Error while sending out email: {}", mailException.getStackTrace());
            log.error("\n ", mailException.getMessage());
            return new ResponseEntity<>("Unable to send email", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Please check your inbox", HttpStatus.OK);
    }

}
