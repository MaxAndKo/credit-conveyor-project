package com.konchalovmaxim.gatewayms.feign;

import com.konchalovmaxim.gatewayms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.gatewayms.dto.LoanOfferDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@FeignClient(value = "application-ms", url = "http://localhost:8082/application")
public interface FeignApplication {

    @PostMapping("/")
    @Headers({"Content-Type: application/json"})
    List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO loanApplicationRequestDTO);

    @PutMapping("/offer")
    @Headers({"Content-Type: application/json"})
    void acceptOffer(LoanOfferDTO loanOfferDTO);
}
