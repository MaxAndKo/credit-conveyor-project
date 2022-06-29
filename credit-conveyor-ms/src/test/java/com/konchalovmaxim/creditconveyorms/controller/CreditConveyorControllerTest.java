package com.konchalovmaxim.creditconveyorms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konchalovmaxim.creditconveyorms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.creditconveyorms.dto.ScoringDataDTO;
import com.konchalovmaxim.creditconveyorms.enums.EmploymentStatus;
import com.konchalovmaxim.creditconveyorms.utils.TestUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.yaml.snakeyaml.util.ArrayUtils;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CreditConveyorControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void calculationShouldReturnCorrectResult(){
        ScoringDataDTO scoringDataDTO = TestUtils.createScoringDataDto();

        byte[] expected = this.getClass().getClassLoader().getResourceAsStream("expectedJson\\credit.json").readAllBytes();

        byte[] actual = mockMvc.perform(
                    post("/conveyor/calculation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(scoringDataDTO))
                            .accept(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();

            Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void calculationShouldReturnBadRequest(){
        ScoringDataDTO scoringDataDTO = TestUtils.createScoringDataDto();
        scoringDataDTO.setAmount(BigDecimal.valueOf(9999));

            mockMvc.perform(
                    post("/conveyor/calculation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(scoringDataDTO))
                            .accept(MediaType.APPLICATION_JSON)
            ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void calculationShouldReturnNotAcceptable(){
        ScoringDataDTO scoringDataDTO = TestUtils.createScoringDataDto();
        scoringDataDTO.getEmployment().setEmploymentStatus(EmploymentStatus.UNEMPLOYED);

            mockMvc.perform(
                    post("/conveyor/calculation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(scoringDataDTO))
                            .accept(MediaType.APPLICATION_JSON)
            ).andExpect(status().isNotAcceptable());
    }

    @Test
    @SneakyThrows
    void offerShouldReturnCorrectResult(){
        LoanApplicationRequestDTO  loanApplicationRequestDTO = TestUtils.createLoanApplicationRequestDTO();

        byte[] expected = this.getClass().getClassLoader().getResourceAsStream("expectedJson\\offer.json").readAllBytes();

        byte[] actual = mockMvc.perform(
                    post("/conveyor/offers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loanApplicationRequestDTO))
                            .accept(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();

        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void offerShouldReturnBadRequest(){
        LoanApplicationRequestDTO  loanApplicationRequestDTO = TestUtils.createLoanApplicationRequestDTO();
        loanApplicationRequestDTO.setFirstName("a");

            mockMvc.perform(
                    post("/conveyor/offers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loanApplicationRequestDTO))
                            .accept(MediaType.APPLICATION_JSON)
            ).andExpect(status().isBadRequest());
    }

}
