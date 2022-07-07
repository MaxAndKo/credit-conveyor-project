package com.konchalovmaxim.dealms.controller;

import com.konchalovmaxim.dealms.dto.*;
import com.konchalovmaxim.dealms.entity.Application;
import com.konchalovmaxim.dealms.entity.Client;
import com.konchalovmaxim.dealms.entity.Credit;
import com.konchalovmaxim.dealms.entity.LoanOffer;
import com.konchalovmaxim.dealms.enums.ApplicationStatus;
import com.konchalovmaxim.dealms.enums.ChangeType;
import com.konchalovmaxim.dealms.exception.CreditConveyorResponseException;
import com.konchalovmaxim.dealms.exception.NonexistentApplication;
import com.konchalovmaxim.dealms.service.ApplicationService;
import com.konchalovmaxim.dealms.service.ClientService;
import com.konchalovmaxim.dealms.service.DealService;
import com.konchalovmaxim.dealms.service.ScoringService;
import com.konchalovmaxim.dealms.util.FeignServiceUtil;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
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


}
