package com.konchalovmaxim.creditconveyorms.service;

import com.konchalovmaxim.creditconveyorms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.creditconveyorms.dto.LoanOfferDTO;

import java.util.List;

public interface OfferService {
    List<LoanOfferDTO> createOffers(LoanApplicationRequestDTO preScoredRequest);
}
