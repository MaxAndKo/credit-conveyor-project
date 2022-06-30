package com.konchalovmaxim.creditconveyorms.controller;

import com.konchalovmaxim.creditconveyorms.dto.CreditDTO;
import com.konchalovmaxim.creditconveyorms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.creditconveyorms.dto.LoanOfferDTO;
import com.konchalovmaxim.creditconveyorms.dto.ScoringDataDTO;
import com.konchalovmaxim.creditconveyorms.service.CreditService;
import com.konchalovmaxim.creditconveyorms.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/conveyor")
@Validated
@RequiredArgsConstructor
public class CreditConveyorController {

    private final OfferService offerService;
    private final CreditService creditService;

    @PostMapping("/offers")
    public List<LoanOfferDTO> offerCalculation(@Valid @RequestBody LoanApplicationRequestDTO applicationRequestDTO){
            return offerService.createOffers(applicationRequestDTO);
    }


    @PostMapping("/calculation")
    public CreditDTO creditCalculation(@Valid @RequestBody ScoringDataDTO dataDTO){
            return creditService.createCredit(dataDTO);
    }
}
