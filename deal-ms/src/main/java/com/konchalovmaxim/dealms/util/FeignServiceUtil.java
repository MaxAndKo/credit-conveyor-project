package com.konchalovmaxim.dealms.util;

import com.konchalovmaxim.dealms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.dealms.dto.LoanOfferDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(value = "credit-conveyor-ms", url = "http://localhost:8080/conveyor")
public interface FeignServiceUtil {

    @PostMapping("/offers")
    @Headers({"Content-Type: application/json"})
    List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO);
}
