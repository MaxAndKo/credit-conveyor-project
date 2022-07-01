package com.konchalovmaxim.dealms.controller;

import com.konchalovmaxim.dealms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.dealms.dto.LoanOfferDTO;
import com.konchalovmaxim.dealms.dto.ScoringDataDTO;
import com.konchalovmaxim.dealms.entity.Application;
import com.konchalovmaxim.dealms.entity.ApplicationStatusHistory;
import com.konchalovmaxim.dealms.entity.Client;
import com.konchalovmaxim.dealms.entity.LoanOffer;
import com.konchalovmaxim.dealms.enums.ApplicationStatus;
import com.konchalovmaxim.dealms.enums.ChangeType;
import com.konchalovmaxim.dealms.exception.CreditConveyorResponseException;
import com.konchalovmaxim.dealms.exception.NonexistentApplication;
import com.konchalovmaxim.dealms.service.ApplicationService;
import com.konchalovmaxim.dealms.service.ClientService;
import com.konchalovmaxim.dealms.service.PassportService;
import com.konchalovmaxim.dealms.util.FeignServiceUtil;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("deal")
@Validated
@RequiredArgsConstructor
public class DealController {

    private final ClientService clientService;
    private final ApplicationService applicationService;
    private final FeignServiceUtil feignServiceUtil;
    private final PassportService passportService;

    @PostMapping("/application")//TODO перед самой сдачей проверить, правильно ли расставляются статусы
    public List<LoanOfferDTO> createApplication(@RequestBody @Valid LoanApplicationRequestDTO requestDTO) {
//        if (passportService
//                .notExistWithSeriesAndNumber(
//                        requestDTO.getPassportSeries(),
//                        requestDTO.getPassportNumber())) {//TODO надо ли вообще делать такую проверку?
            Client client = new Client(requestDTO);

            client = clientService.saveOrReturnExists(client);
            Application application = new Application(client);

        try {
            application.setCreationDate(LocalDate.now());
            application = applicationService.save(application);

            List<LoanOfferDTO> loanOfferDTOS = feignServiceUtil.getLoanOffers(requestDTO);

            application.setStatus(ApplicationStatus.PREAPPROVAL);

            for (LoanOfferDTO loanOfferDTO : loanOfferDTOS) {
                loanOfferDTO.setApplicationId(application.getId());
            }

            return loanOfferDTOS;
            }
            catch (FeignException.FeignClientException e){
                application.setStatus(ApplicationStatus.CC_DENIED);
                String message = e.getMessage();
                int startOfError = message.indexOf("error") + 8;
                int endOfError = message.length() - 3;
                throw new CreditConveyorResponseException(message.substring(startOfError, endOfError));
            }

//        }
//        throw new ClientAlredyExistsException("Такой клиент уже зарегистрирован");
    }

    @PutMapping("/offer")
    public void acceptOffer(@RequestBody @Valid LoanOfferDTO loanOfferDTO) {
        Application application = applicationService.findById(loanOfferDTO.getApplicationId());

        if (application == null) {
            throw new NonexistentApplication("Заявки с таким id не существует");
        } else {
            application.getStatusHistories().add(new ApplicationStatusHistory(ChangeType.MANUAL, application));
            application.setStatus(ApplicationStatus.APPROVED);
            application.setLoanOffer(new LoanOffer(loanOfferDTO));
            applicationService.save(application);//TODO проверить будет ли работать без принудительного сохранения
        }
    }

    @PutMapping("/calculate/{applicationId}")
    public void finishCalculation(@RequestBody @Valid ScoringDataDTO scoringDataDTO, @PathVariable Long applicationId) {
        //TODO выяснить, что должно приходить в запросе
    }
}
