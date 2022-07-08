package com.konchalovmaxim.dealms.service;

import com.konchalovmaxim.dealms.dto.FinishRegistrationRequestDTO;
import com.konchalovmaxim.dealms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.dealms.dto.LoanOfferDTO;

import java.util.List;

public interface DealService {
    List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO requestDTO);
    void acceptOffer(LoanOfferDTO loanOfferDTO);
    public void finishCalculation(FinishRegistrationRequestDTO requestDTO, Long applicationId);
}
