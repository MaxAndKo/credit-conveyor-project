package com.konchalovmaxim.applicationms.handler;

import com.konchalovmaxim.applicationms.dto.ErrorDTO;
import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.ConnectException;
import java.util.Date;

@ControllerAdvice
public class ApplicationExceptionHandler {
    @ExceptionHandler(value = ConnectException.class)
    protected ResponseEntity<ErrorDTO> handleConnectException(ConnectException exception) {
        return ResponseEntity.status(500)
                .body(new ErrorDTO(new Date(), "500", "В данный момент сервис недоступен"));
    }

    @ExceptionHandler(value = FeignException.InternalServerError.class)
    protected ResponseEntity<ErrorDTO> handleInternalServerError(FeignException.InternalServerError error) {
        return ResponseEntity.status(500)
                .body(new ErrorDTO(new Date(), "500", correctMessage(error.getMessage())));
    }

    @ExceptionHandler(value = FeignException.BadRequest.class)
    protected ResponseEntity<ErrorDTO> handleBadRequestException(FeignException.BadRequest exception) {
        return ResponseEntity.status(400)
                .body(new ErrorDTO(new Date(), "400", correctMessage(exception.getMessage())));
    }

    @ExceptionHandler(value = FeignException.NotAcceptable.class)
    protected ResponseEntity<ErrorDTO> handleNotAcceptable(FeignException.NotAcceptable exception) {
        return ResponseEntity.status(406)
                .body(new ErrorDTO(new Date(), "406", correctMessage(exception.getMessage())));
    }

    private String correctMessage(String message) {
        int startOfError = message.indexOf("error") + 8;
        int endOfError = message.length() - 3;
        return message.substring(startOfError, endOfError);
    }

}
