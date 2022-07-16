package com.konchalovmaxim.dossier.util;

import com.konchalovmaxim.dossier.dto.DocumentDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;


@FeignClient(value = "deal-ms", url = "http://localhost:8081/deal")
public interface FeignUtil {
    @GetMapping("/document/{applicationId}")
    @Headers({"Content-Type: application/json"})
    DocumentDTO getDocumentDTO(@PathVariable("applicationId") Long applicationId);

    @PutMapping("/document/{applicationId}/code")
    @Headers({"Content-Type: application/json"})
    String getSesCode(@PathVariable("applicationId") Long applicationId);
}
