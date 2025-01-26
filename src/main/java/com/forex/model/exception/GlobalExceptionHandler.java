package com.forex.model.exception;

import com.forex.model.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExternalApiException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDTO handleExternalApiException(Exception e) {
        e.printStackTrace();
        return generateErrorDTO(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ValidationException.class) // Add handler for ValidationException
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleValidationException(ValidationException e) {
        e.printStackTrace();
        return generateErrorDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleMissingRequestParam(MissingServletRequestParameterException e) {
        String errorMessage = String.format("Missing required request parameter: %s", e.getParameterName());
        return generateErrorDTO(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errorMessages = new ArrayList<>();

        e.getBindingResult().getFieldErrors().forEach(error -> {
            String errorMessage = error.getDefaultMessage();
            errorMessages.add(errorMessage);
        });

        return generateErrorDTO(errorMessages, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleHandlerMethodValidationExceptions(HandlerMethodValidationException e) {
        Map<String, String> errors = new HashMap<>();

        e.getAllErrors().forEach(error -> {
            if (error instanceof FieldError) {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            } else {
                String objectName = error.getClass().getName();
                String errorMessage = error.getDefaultMessage();
                errors.put(objectName, errorMessage);
            }
        });

        return generateErrorDTO(errors, HttpStatus.BAD_REQUEST);
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
