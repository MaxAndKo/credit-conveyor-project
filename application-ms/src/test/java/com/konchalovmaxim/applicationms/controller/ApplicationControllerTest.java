package com.konchalovmaxim.applicationms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konchalovmaxim.applicationms.config.HttpProperties;
import com.konchalovmaxim.applicationms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.applicationms.dto.LoanOfferDTO;
import com.konchalovmaxim.applicationms.filter.HttpMessageLogFormatter;
import com.konchalovmaxim.applicationms.service.ApplicationService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicationController.class)
@ContextConfiguration(classes = ApplicationControllerTest.TestConf.class)
public class ApplicationControllerTest {

    @MockBean
    private ApplicationService applicationService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private static final Long applicationId = 5L;


    @Test
    void loadContext(){
        assertNotNull(applicationService);
        assertNotNull(mockMvc);
        assertNotNull(objectMapper);
    }

    @Test
    @SneakyThrows
    void createApplicationShouldReturnBadRequest() {

        LoanApplicationRequestDTO loanApplicationRequestDTO = new LoanApplicationRequestDTO();

        mockMvc.perform(
                post("/application/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanApplicationRequestDTO))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void createApplicationShouldReturnOk() {

        LoanApplicationRequestDTO loanApplicationRequestDTO = getCorrectLoanApplicationRequestDTO();
        when(applicationService.createApplication(any())).thenReturn(new ArrayList<>());

        mockMvc.perform(
                post("/application/")
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
                put("/application/offer")
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
                put("/application/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanOfferDTO))
        ).andExpect(status().isOk());
    }

    private static LoanApplicationRequestDTO getCorrectLoanApplicationRequestDTO() {
        LoanApplicationRequestDTO loanDTO = new LoanApplicationRequestDTO();

        loanDTO.setAmount(BigDecimal.valueOf(300000));
        loanDTO.setFirstName("Ivan");
        loanDTO.setLastName("Ivanov");
        loanDTO.setTerm(6);
        loanDTO.setBirthdate(LocalDate.of(1998, 12, 12));
        loanDTO.setPassportSeries("1234");
        loanDTO.setPassportNumber("567890");
        loanDTO.setEmail("sghsfg@pochta.su");

        return loanDTO;
    }

    public static LoanOfferDTO getCorrectLoanOfferDTO() {
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO();
        loanOfferDTO.setApplicationId(applicationId);
        loanOfferDTO.setRequestedAmount(BigDecimal.valueOf(10000));
        loanOfferDTO.setTotalAmount(BigDecimal.valueOf(12000));
        loanOfferDTO.setTerm(6);
        loanOfferDTO.setMonthlyPayment(BigDecimal.valueOf(2000));
        loanOfferDTO.setRate(BigDecimal.valueOf(20));
        loanOfferDTO.setIsInsuranceEnabled(false);
        loanOfferDTO.setIsSalaryClient(false);
        return loanOfferDTO;
    }

    @TestConfiguration
    public static class TestConf {
        @Bean
        HttpMessageLogFormatter httpmessageLogFormatter() {
            return new HttpMessageLogFormatter();
        }
        @Bean
        HttpProperties httpProperties(){
            return new HttpProperties();
        }
    }
}


