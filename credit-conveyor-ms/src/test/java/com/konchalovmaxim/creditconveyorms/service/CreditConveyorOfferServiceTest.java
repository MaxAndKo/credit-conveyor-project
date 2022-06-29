package com.konchalovmaxim.creditconveyorms.service;

import com.konchalovmaxim.creditconveyorms.dto.*;
import com.konchalovmaxim.creditconveyorms.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CreditConveyorOfferServiceTest {

    @Autowired
    OfferService offerService;

    @Test
    void create4OffersShouldReturnsFourCorrectOffers() {
        List<LoanOfferDTO> actual = offerService.createOffers(TestUtils.createLoanApplicationRequestDTO());

        List<LoanOfferDTO> expected = createExpectedOffers();

        assertEquals(expected, actual);
    }

    private List<LoanOfferDTO> createExpectedOffers(){
        List<LoanOfferDTO> expected = new ArrayList<>(4);
        expected.add(
                new LoanOfferDTO(
                        0L,
                        BigDecimal.valueOf(300000),
                        BigDecimal.valueOf(360191.34),
                        18, BigDecimal.valueOf(20010.63),
                        BigDecimal.valueOf(24),
                        false,
                        false));

        expected.add(
                new LoanOfferDTO(
                        0L,
                        BigDecimal.valueOf(300000),
                        BigDecimal.valueOf(354934.80).setScale(2, RoundingMode.HALF_UP),
                        18,
                        BigDecimal.valueOf(19718.60).setScale(2, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(22),
                        true,
                        false));

        expected.add(
                new LoanOfferDTO(
                        0L,
                        BigDecimal.valueOf(300000),
                        BigDecimal.valueOf(352322.64),
                        18,
                        BigDecimal.valueOf(19573.48),
                        BigDecimal.valueOf(21),
                        false,
                        true));

        expected.add(
                new LoanOfferDTO(
                        0L,
                        BigDecimal.valueOf(300000),
                        BigDecimal.valueOf(347130.90).setScale(2, RoundingMode.HALF_UP),
                        18,
                        BigDecimal.valueOf(19285.05),
                        BigDecimal.valueOf(19),
                        true,
                        true));

        return expected;
    }



}