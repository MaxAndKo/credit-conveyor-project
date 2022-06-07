package com.konchalovmaxim.creditconveyorms.controller;

import com.konchalovmaxim.creditconveyorms.dto.CreditDTO;
import com.konchalovmaxim.creditconveyorms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.creditconveyorms.dto.LoanOfferDTO;
import com.konchalovmaxim.creditconveyorms.dto.ScoringDataDTO;
import com.konchalovmaxim.creditconveyorms.service.CreditService;
import com.konchalovmaxim.creditconveyorms.service.OfferService;
import com.konchalovmaxim.creditconveyorms.service.ScoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/conveyor")
@Validated
public class CreditConveyorController {

    private final OfferService offerService;
    private final CreditService creditService;

    public CreditConveyorController(OfferService offerService, CreditService creditService) {
        this.offerService = offerService;
        this.creditService = creditService;
    }

    @PostMapping("/offers")
    public List<LoanOfferDTO> offerCalculation(@Valid @RequestBody LoanApplicationRequestDTO applicationRequestDTO){
            return offerService.createFourOffers(applicationRequestDTO);
    }


    @PostMapping("/calculation")
    public CreditDTO creditCalculation(@Valid @RequestBody ScoringDataDTO dataDTO){
            return creditService.createCredit(dataDTO);
    }
}
