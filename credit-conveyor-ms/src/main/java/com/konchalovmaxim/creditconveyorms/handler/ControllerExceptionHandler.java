package com.konchalovmaxim.creditconveyorms.handler;

import com.konchalovmaxim.creditconveyorms.dto.ErrorDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.util.Date;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(value = {javax.validation.ConstraintViolationException.class})
    protected ResponseEntity<ErrorDTO> handleValidationExceptions(ConstraintViolationException e, WebRequest request){

        return new ResponseEntity<ErrorDTO>(new ErrorDTO(new Date(), "Bad Request",
                e.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST);

    }
}
