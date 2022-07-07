package com.konchalovmaxim.dealms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konchalovmaxim.dealms.dto.FinishRegistrationRequestDTO;
import com.konchalovmaxim.dealms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.dealms.dto.LoanOfferDTO;
import com.konchalovmaxim.dealms.filter.HttpMessageLogFormatter;
import com.konchalovmaxim.dealms.service.DealService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static com.konchalovmaxim.dealms.util.TestUtil.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DealController.class)
@ContextConfiguration(classes = DealControllerTest.TestConf.class)
class DealControllerTest {
    @MockBean
    private DealService dealService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void uploadContext() {
        Assertions.assertNotNull(mockMvc);
    }

    @Test
    @SneakyThrows
    void createApplicationShouldReturnBadRequest() {

        LoanApplicationRequestDTO loanApplicationRequestDTO = new LoanApplicationRequestDTO();

        mockMvc.perform(
                post("/deal/application")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanApplicationRequestDTO))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void createApplicationShouldReturnOk() {

        LoanApplicationRequestDTO loanApplicationRequestDTO = getCorrectLoanApplicationRequestDTO();
        when(dealService.createApplication(any())).thenReturn(new ArrayList<>());

        mockMvc.perform(
                post("/deal/application")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanApplicationRequestDTO))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void acceptOfferShouldReturnBadRequest() {

        LoanOfferDTO loanOfferDTO = new LoanOfferDTO();

        mockMvc.perform(
                put("/deal/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanOfferDTO))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void acceptOfferShouldReturnOk() {

        LoanOfferDTO loanOfferDTO = getCorrectLoanOfferDTO();
        mockMvc.perform(
                put("/deal/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanOfferDTO))
        ).andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void finishCalculationShouldReturnBadRequest() {

        FinishRegistrationRequestDTO finishRegistrationRequestDTO = new FinishRegistrationRequestDTO();

        mockMvc.perform(
                put("/deal/calculate/" + applicationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(finishRegistrationRequestDTO))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void finishCalculationShouldReturnOk() {

        FinishRegistrationRequestDTO finishRegistrationRequestDTO = getCorrectFinishRegistrationRequestDTO();

        mockMvc.perform(
                put("/deal/calculate/" + applicationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(finishRegistrationRequestDTO))
        ).andExpect(status().isOk());
    }


    @TestConfiguration
    public static class TestConf {
        @Bean
        HttpMessageLogFormatter httpmessageLogFormatter() {
            return new HttpMessageLogFormatter();
        }
    }
}