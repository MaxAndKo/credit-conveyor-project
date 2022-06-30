package com.konchalovmaxim.creditconveyorms.service;

import com.konchalovmaxim.creditconveyorms.dto.CreditDTO;
import com.konchalovmaxim.creditconveyorms.dto.ScoringDataDTO;


public interface CreditService {
    CreditDTO createCredit(ScoringDataDTO scoringDataDTO);
}
