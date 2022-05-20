package com.konchalovmaxim.creditconveyorms.controller;

import com.konchalovmaxim.creditconveyorms.dto.CreditDTO;
import com.konchalovmaxim.creditconveyorms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.creditconveyorms.dto.LoanOfferDTO;
import com.konchalovmaxim.creditconveyorms.dto.ScoringDataDTO;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.konchalovmaxim.creditconveyorms.utils.creditConveyorUtils.*;

@RestController
@RequestMapping("/conveyor")
public class CreditConveyorController {

    @PostMapping("/offers")
    public List<LoanOfferDTO> offerСalculation(@Valid @RequestBody LoanApplicationRequestDTO applicationRequestDTO,
                                               BindingResult bindingResult){
        if (!bindingResult.hasErrors() && getAge(applicationRequestDTO.getBirthdate()) >= 18){
            return create4Offers(applicationRequestDTO);
        }

        return null;
    }


    @PostMapping("/calculation")
    public CreditDTO creditCalculation(@Valid @RequestBody ScoringDataDTO dataDTO,
                                       BindingResult bindingResult){
        if (!bindingResult.hasErrors()){
            return createCredit(dataDTO);
        }

        return null;
    }
}
