package com.konchalovmaxim.creditconveyorms.handler;

import com.konchalovmaxim.creditconveyorms.dto.ErrorDTO;
import com.konchalovmaxim.creditconveyorms.exception.CreditNotAvailableException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class CreditConveyorServiceExceptionHandler {
    @ExceptionHandler(value = {CreditNotAvailableException.class})
    protected ResponseEntity<ErrorDTO> handleValidationExceptions(CreditNotAvailableException e){

        return new ResponseEntity<ErrorDTO>(new ErrorDTO(new Date(), "406",
                e.getMessage()), new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE);

    }
}
