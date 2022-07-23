package com.konchalovmaxim.applicationms.utils;

import com.konchalovmaxim.applicationms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.applicationms.dto.LoanOfferDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@FeignClient(value = "deal-ms", url = "http://localhost:8081/deal")
public interface FeignUtil {
    @PostMapping("/application")
    @Headers({"Content-Type: application/json"})
    List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO);

    @PutMapping("/offer")
    @Headers({"Content-Type: application/json"})
    void acceptOffer(LoanOfferDTO loanOfferDTO);
}
