package com.forex.exception;

import com.forex.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExternalApiException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDTO handleExternalApiException(Exception e) {
        e.printStackTrace();
        return generateErrorDTO(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleValidationException(Exception e) {
        e.printStackTrace();
        return generateErrorDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleRest(Exception e) {
        e.printStackTrace();
        return generateErrorDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorDTO generateErrorDTO(Object o, HttpStatus s) {
        return ErrorDTO.builder()
                .msg(o)
                .time(LocalDateTime.now())
                .status(s.value())
                .build();
    }
}
