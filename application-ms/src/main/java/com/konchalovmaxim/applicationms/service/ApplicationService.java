package com.konchalovmaxim.applicationms.service;

import com.konchalovmaxim.applicationms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.applicationms.dto.LoanOfferDTO;
import com.konchalovmaxim.applicationms.exception.UnderageException;
import com.konchalovmaxim.applicationms.utils.FeignUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

    private final FeignUtil feignUtil;

    public List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO requestDTO) {
        log.debug("Incoming LoanApplicationRequestDTO: {}", requestDTO);

        if (getAge(requestDTO.getBirthdate()) < 18) {
            log.debug("Exception by birth date: {}", requestDTO.getBirthdate());

            throw new UnderageException("Клиенту нет 18 лет");
        } else {
            List<LoanOfferDTO> loanOfferDTOS = feignUtil.getLoanOffers(requestDTO);
            log.debug("Received loanOfferDTOS: {}", loanOfferDTOS);

            return loanOfferDTOS;
        }
    }

    public void acceptOffer(LoanOfferDTO loanOfferDTO) {
        log.debug("Incoming LoanOfferDTO: {}", loanOfferDTO);

        feignUtil.acceptOffer(loanOfferDTO);
        log.debug("acceptOffer completed successfully");
    }

    private int getAge(LocalDate birthDate) {
        log.debug("Incoming birthDate: {}", birthDate);

        int age = Period.between(birthDate, LocalDate.now()).getYears();
        log.debug("Calculated age: {}", age);

        return age;
    }

}
