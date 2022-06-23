package com.konchalovmaxim.creditconveyorms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konchalovmaxim.creditconveyorms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.creditconveyorms.dto.ScoringDataDTO;
import com.konchalovmaxim.creditconveyorms.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CreditConveyorControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void calculationShouldReturnOk(){
        ScoringDataDTO scoringDataDTO = TestUtils.createScoringDataDto();

        try {
            mockMvc.perform(
                    post("/conveyor/calculation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(scoringDataDTO))
                            .accept(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void calculationShouldReturnBadRequest(){
        ScoringDataDTO scoringDataDTO = TestUtils.createScoringDataDto();
        scoringDataDTO.setAmount(BigDecimal.valueOf(9999));

        try {
            mockMvc.perform(
                    post("/conveyor/calculation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(scoringDataDTO))
                            .accept(MediaType.APPLICATION_JSON)
            ).andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void calculationShouldReturnMethodNotAllowed(){
        ScoringDataDTO scoringDataDTO = TestUtils.createScoringDataDto();

        try {
            mockMvc.perform(
                    get("/conveyor/calculation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(scoringDataDTO))
                            .accept(MediaType.APPLICATION_JSON)
            ).andExpect(status().isMethodNotAllowed());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void offerShouldReturnOk(){
        LoanApplicationRequestDTO  loanApplicationRequestDTO = TestUtils.createLoanApplicationRequestDTO();

        try {
            mockMvc.perform(
                    post("/conveyor/offers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loanApplicationRequestDTO))
                            .accept(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void offerShouldReturnBadRequest(){
        LoanApplicationRequestDTO  loanApplicationRequestDTO = TestUtils.createLoanApplicationRequestDTO();
        loanApplicationRequestDTO.setFirstName("a");

        try {
            mockMvc.perform(
                    post("/conveyor/offers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loanApplicationRequestDTO))
                            .accept(MediaType.APPLICATION_JSON)
            ).andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void offerShouldReturnMethodNotAllowed(){
        LoanApplicationRequestDTO  loanApplicationRequestDTO = TestUtils.createLoanApplicationRequestDTO();

        try {
            mockMvc.perform(
                    get("/conveyor/offers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loanApplicationRequestDTO))
                            .accept(MediaType.APPLICATION_JSON)
            ).andExpect(status().isMethodNotAllowed());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
