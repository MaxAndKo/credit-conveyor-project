package com.konchalovmaxim.gatewayms.controller;

import com.konchalovmaxim.gatewayms.dto.FinishRegistrationRequestDTO;
import com.konchalovmaxim.gatewayms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.gatewayms.dto.LoanOfferDTO;
import com.konchalovmaxim.gatewayms.feign.FeignApplication;
import com.konchalovmaxim.gatewayms.feign.FeignDeal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/gateway")
public class GatewayController {
    private final FeignApplication feignApplication;
    private final FeignDeal feignDeal;

    @PostMapping("/application")
    public List<LoanOfferDTO> createApplication(@RequestBody @Valid LoanApplicationRequestDTO requestDTO){
        List<LoanOfferDTO> offers = feignApplication.createApplication(requestDTO);
        log.info("Return offers: {}", offers);

        return offers;
    }

    @PostMapping("/application/apply")
    public void acceptOffer(@RequestBody @Valid LoanOfferDTO loanOfferDTO){
        log.info("sendDocuments received LoanOfferDTO: {}", loanOfferDTO);

        feignApplication.acceptOffer(loanOfferDTO);
        log.info("Offer successfully sent");
    }

    @PostMapping("/application/registration/{applicationId}")
    public void calculateCredit(@RequestBody @Valid FinishRegistrationRequestDTO requestDTO,
                                @PathVariable(required = true) Long applicationId){
        log.info("sendDocuments received FinishRegistrationRequestDTO: {}.\nApplicationId: {}", requestDTO, applicationId);
        feignDeal.calculateCredit(requestDTO, applicationId);
        log.info("FinishRegistrationRequestDTO successfully sent");
    }

    @PostMapping("/document/{applicationId}")
    public void sendDocuments(@PathVariable(required = true) Long applicationId){
        log.info("sendDocuments received applicationId: {}", applicationId);
        feignDeal.sendDocuments(applicationId);
        log.info("Documents successfully sent");
    }

    @PostMapping("/document/{applicationId}/sign")
    public void signDocuments(@PathVariable(required = true) Long applicationId){
        log.info("signDocuments received applicationId: {}", applicationId);
        feignDeal.signDocuments(applicationId);
        log.info("Sign request successfully sent");
    }

    @PostMapping("/document/{applicationId}/sign/code")
    public void verifySesCode(@PathVariable(required = true) Long applicationId, @RequestBody @Valid String code){
        log.info("verifySesCode received applicationId: {}", applicationId);
        feignDeal.verifySesCode(applicationId, code);
        log.info("Code successfully sent");
    }

    @PutMapping("/cancel/{applicationId}")
    public void cancelApplication(@PathVariable(required = true) Long applicationId){
        log.info("cancelApplication received applicationId: {}", applicationId);
        feignDeal.cancelApplication(applicationId);
        log.info("Application successfully canceled");
    }

}
