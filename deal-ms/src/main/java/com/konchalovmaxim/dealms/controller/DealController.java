package com.konchalovmaxim.dealms.controller;

import com.konchalovmaxim.dealms.dto.*;
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
        dealService.requireDocumentSend(applicationId);
    }

    @GetMapping("/application/{applicationId}")
    public DocumentDTO getDocument(@PathVariable("applicationId") Long applicationId){
        return dealService.getDocument(applicationId);
    }

//
//    @PostMapping("/document/{applicationId}/sign")
//    public void documentsign(@PathVariable("applicationId") Long applicationId) {
//    }
//
//    @PostMapping("/document/{applicationId}/code")
//    public void documentCode(@PathVariable("applicationId") Long applicationId) {
//    }

}
