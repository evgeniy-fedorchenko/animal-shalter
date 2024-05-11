package com.evgeniyfedorchenko.animalshelter.backend.exceptions.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)     // Нарушение целостности базы данных
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {

        StringBuilder errMessBuilder = new StringBuilder("Violations of data integrity: \n");
        Set<ConstraintViolation<?>> constraintViolations;

    /* Чаще всего спринговый org.springframework.dao.DataIntegrityViolationException возникает из-за того, что в БД
       хотели положить что-то неподходящее для неё, то есть из-за jakarta.validation.ConstraintViolationException */
        if (ex.getCause() instanceof ConstraintViolationException cve) {

            constraintViolations = cve.getConstraintViolations();
            IntStream.range(0, constraintViolations.size())
                    .forEach(_ -> errMessBuilder.append(". ")
                            .append(constraintViolations.iterator().next().getMessage())
                            .append("\n"));
        } else {
            constraintViolations = null;
            errMessBuilder.append(ex.getMessage());
        }

        ResponseEntity<String> body = ResponseEntity.status(HttpStatus.CONFLICT).body(errMessBuilder.toString());
        if (constraintViolations == null) {
            log.info("Exception {} handled. Cause: {}", ex.getClass().getSimpleName(), ex.getMessage());
        } else {
            log.debug("In inputParam(s) were found {} invalidValue, response body = {}", constraintViolations.size(), body.getBody());
        }
        return body;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)    // Невалидные параметры в контроллере
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        StringBuilder errMessBuilder = new StringBuilder("Validation errors:\n");
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        IntStream.range(0, fieldErrors.size())
                .forEach(i -> errMessBuilder.append(i + 1)
                        .append(". ")
                        .append(fieldErrors.get(i).getField())
                        .append(". Cause: ")
                        .append(fieldErrors.get(i).getDefaultMessage())
                        .append("\n")
                );
        ResponseEntity<String> body = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errMessBuilder.toString());

        log.debug("In inputDto were found {} invalidParams, response body = {}", fieldErrors.size(), body.getBody());
        return body;
    }

    @ExceptionHandler(ValidationException.class)   // Нарушение
    public ResponseEntity<String> handleValidationException(ValidationException ex) {
        ResponseEntity<String> body = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        log.debug("In inputDto were found invalid param, response body = {}", body);
        return body;
    }
}
