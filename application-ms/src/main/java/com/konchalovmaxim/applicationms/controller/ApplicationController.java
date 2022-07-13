package com.konchalovmaxim.applicationms.controller;

import com.konchalovmaxim.applicationms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.applicationms.dto.LoanOfferDTO;
import com.konchalovmaxim.applicationms.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
@Validated
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/")
    public List<LoanOfferDTO> createApplication(@RequestBody @Valid LoanApplicationRequestDTO requestDTO){
        return applicationService.createApplication(requestDTO);
    }

    @PutMapping("/offer")
    public void acceptOffer(@RequestBody @Valid LoanOfferDTO loanOfferDTO){
        applicationService.acceptOffer(loanOfferDTO);
    }
}
