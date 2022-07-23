package com.konchalovmaxim.gatewayms.feign;

import com.konchalovmaxim.gatewayms.dto.FinishRegistrationRequestDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(value = "deal-ms", url = "http://localhost:8081/deal")
public interface FeignDeal {

    @PutMapping("/calculate/{applicationId}")
    @Headers({"Content-Type: application/json"})
    void calculateCredit(FinishRegistrationRequestDTO requestDTO,
                         @PathVariable Long applicationId);

    @PostMapping("/document/{applicationId}/send")
    @Headers({"Content-Type: application/json"})
    void sendDocuments(@PathVariable Long applicationId);

    @PostMapping("/document/{applicationId}/sign")
    @Headers({"Content-Type: application/json"})
    void signDocuments(@PathVariable Long applicationId);

    @PostMapping("/document/{applicationId}/code")
    @Headers({"Content-Type: application/json"})
    void verifySesCode(@PathVariable Long applicationId, String code);

    @PutMapping("/application/{applicationId}")
    @Headers({"Content-Type: application/json"})
    void cancelApplication(@PathVariable Long applicationId);
}
