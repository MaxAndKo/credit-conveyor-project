package com.konchalovmaxim.dealms.handler;

import com.konchalovmaxim.dealms.dto.ErrorDTO;
import com.konchalovmaxim.dealms.exception.ClientAlredyExistsException;
import com.konchalovmaxim.dealms.exception.CreditConveyorResponseException;
import com.konchalovmaxim.dealms.exception.ApplicationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.ConnectException;
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

    @ExceptionHandler(value = {ApplicationException.class})
    protected ResponseEntity<ErrorDTO> handleNonexistentApplicationException(ApplicationException exception){
        return ResponseEntity.status(400)
                .body(new ErrorDTO(new Date(), "400", exception.getMessage()));
    }
        @ExceptionHandler(value = ConnectException.class)
    protected ResponseEntity<ErrorDTO> handleConnectException(ConnectException exception){
        return ResponseEntity.status(500)
                .body(new ErrorDTO(new Date(), "500", "В данный момент сервис недоступен"));
    }
}
