package com.konchalovmaxim.creditconveyorms.controller;

import com.konchalovmaxim.creditconveyorms.dto.CreditDTO;
import com.konchalovmaxim.creditconveyorms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.creditconveyorms.dto.LoanOfferDTO;
import com.konchalovmaxim.creditconveyorms.dto.ScoringDataDTO;
import com.konchalovmaxim.creditconveyorms.service.CreditConveyorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/conveyor")
public class CreditConveyorController {

    @Autowired
    private CreditConveyorService creditConveyorService;

    @PostMapping("/offers")
    public List<LoanOfferDTO> offerСalculation(@Valid @RequestBody LoanApplicationRequestDTO applicationRequestDTO,
                                               BindingResult bindingResult){
        if (!bindingResult.hasErrors() && creditConveyorService.getAge(applicationRequestDTO.getBirthdate()) >= 18){
            return creditConveyorService.create4Offers(applicationRequestDTO);
        }

        return null;
    }


    @PostMapping("/calculation")
    public CreditDTO creditCalculation(@Valid @RequestBody ScoringDataDTO dataDTO,
                                       BindingResult bindingResult){
        if (!bindingResult.hasErrors()){
            return creditConveyorService.createCredit(dataDTO);
        }

        return null;
    }
}
