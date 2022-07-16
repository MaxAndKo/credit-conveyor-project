package com.konchalovmaxim.dealms.service;

import com.konchalovmaxim.dealms.dto.DocumentDTO;
import com.konchalovmaxim.dealms.dto.FinishRegistrationRequestDTO;
import com.konchalovmaxim.dealms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.dealms.dto.LoanOfferDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DealService {
    List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO requestDTO);
    void acceptOffer(LoanOfferDTO loanOfferDTO);
    void finishCalculation(FinishRegistrationRequestDTO requestDTO, Long applicationId);

    void requireDocumentSend(Long applicationId);

    DocumentDTO getDocument(Long applicationId);

    @Transactional
    void requireSes(Long applicationId);

    @Transactional
    String getSes(Long applicationId);

    void documentCode(Long applicationId, String code);

    void clientCanceledApplication(Long applicationId);
}
