package com.konchalovmaxim.dealms.service;

import com.konchalovmaxim.dealms.dto.FinishRegistrationRequestDTO;
import com.konchalovmaxim.dealms.dto.ScoringDataDTO;
import com.konchalovmaxim.dealms.entity.Application;

public interface ScoringService {
    ScoringDataDTO prepareScoringData(Application application, FinishRegistrationRequestDTO requestDTO);
}
