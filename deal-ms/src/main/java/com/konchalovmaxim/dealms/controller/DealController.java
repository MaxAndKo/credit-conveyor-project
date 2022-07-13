package com.konchalovmaxim.dealms.controller;

import com.konchalovmaxim.dealms.dto.*;
import com.konchalovmaxim.dealms.enums.Theme;
import com.konchalovmaxim.dealms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("deal")
@Validated
@RequiredArgsConstructor
public class DealController {

    private final DealService dealService;
    private final KafkaProducerService kafkaProducerService;

    @PostMapping("/application")
    public List<LoanOfferDTO> createApplication(@RequestBody @Valid LoanApplicationRequestDTO requestDTO) {
        return dealService.createApplication(requestDTO);
    }

    @PutMapping("/offer")
    public void acceptOffer(@RequestBody @Valid LoanOfferDTO loanOfferDTO) {
        dealService.acceptOffer(loanOfferDTO);
    }

    @PutMapping("/calculate/{applicationId}")
    public void finishCalculation(
            @RequestBody @Valid FinishRegistrationRequestDTO requestDTO,
            @PathVariable(required = true) Long applicationId) {
        dealService.finishCalculation(requestDTO, applicationId);
    }

    @PostMapping("/document/{applicationId}/send")
    public void documentSend(@PathVariable("applicationId") Long applicationId) {
        kafkaProducerService.sendCreateDocuments(new EmailMessageDTO("someEmailAddress", Theme.SEND_DOCUMENTS, applicationId));
    }

    @PostMapping("/document/{applicationId}/sign")
    public void documentsign(@PathVariable("applicationId") Long applicationId) {
    }

    @PostMapping("/document/{applicationId}/code")
    public void documentCode(@PathVariable("applicationId") Long applicationId) {
    }

}
