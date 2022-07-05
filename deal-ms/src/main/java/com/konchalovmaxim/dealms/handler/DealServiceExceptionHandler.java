package com.konchalovmaxim.dealms.handler;

import com.konchalovmaxim.dealms.dto.ErrorDTO;
import com.konchalovmaxim.dealms.exception.ClientAlredyExistsException;
import com.konchalovmaxim.dealms.exception.CreditConveyorResponseException;
import com.konchalovmaxim.dealms.exception.NonexistentApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class DealServiceExceptionHandler {
    @ExceptionHandler(value = {ClientAlredyExistsException.class})
    protected ResponseEntity<ErrorDTO> handleSaveClientException(ClientAlredyExistsException exception){
        return ResponseEntity.status(406)
                .body(new ErrorDTO(new Date(), "406", exception.getMessage()));
    }

    @ExceptionHandler(value = {CreditConveyorResponseException.class})
    protected ResponseEntity<ErrorDTO> handleCreditConveyorException(CreditConveyorResponseException exception){
        return ResponseEntity.status(406)
                .body(new ErrorDTO(new Date(), "406", exception.getMessage()));
    }

    @ExceptionHandler(value = {NonexistentApplication.class})
    protected ResponseEntity<ErrorDTO> handleNonexistentApplicationException(NonexistentApplication exception){
        return ResponseEntity.status(400)
                .body(new ErrorDTO(new Date(), "400", exception.getMessage()));
    }
}
