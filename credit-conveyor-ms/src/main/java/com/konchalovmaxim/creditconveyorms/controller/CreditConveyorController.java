package com.konchalovmaxim.creditconveyorms.controller;

import com.konchalovmaxim.creditconveyorms.dto.CreditDTO;
import com.konchalovmaxim.creditconveyorms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.creditconveyorms.dto.LoanOfferDTO;
import com.konchalovmaxim.creditconveyorms.dto.ScoringDataDTO;
import com.konchalovmaxim.creditconveyorms.service.CreditConveyorService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/conveyor")
@Validated
public class CreditConveyorController {

    private final CreditConveyorService creditConveyorService;

    public CreditConveyorController(CreditConveyorService creditConveyorService) {
        this.creditConveyorService = creditConveyorService;
    }

    @PostMapping("/offers")
    public List<LoanOfferDTO> offerCalculation(@Valid @RequestBody LoanApplicationRequestDTO applicationRequestDTO){
            return creditConveyorService.createFourOffers(applicationRequestDTO);
    }


    @PostMapping("/calculation")
    public CreditDTO creditCalculation(@Valid @RequestBody ScoringDataDTO dataDTO){
            return creditConveyorService.createCredit(dataDTO);
    }
}
