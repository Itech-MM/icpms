package org.flexitech.projects.icpms.api.controllers.handlers;

import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.icpms.dto.api.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleBodyValidation(
            MethodArgumentNotValidException ex) {

        List<ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toValidationError)
                .collect(Collectors.toList());

        ex.getBindingResult().getGlobalErrors().forEach(ge ->
                errors.add(new ValidationError(ge.getObjectName(), ge.getDefaultMessage())));

        String message = errors.isEmpty()
                ? "Validation failed."
                : errors.get(0).getMessage();

        log.warn("Validation failed: {}", errors);

        return ApiResponse.badRequest(message, errors); // see overload below
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleParamValidation(
            ConstraintViolationException ex) {

        List<ValidationError> errors = ex.getConstraintViolations()
                .stream()
                .map(v -> new ValidationError(
                        v.getPropertyPath().toString(),
                        v.getMessage()))
                .collect(Collectors.toList());

        return ApiResponse.badRequest("Validation failed.", errors);
    }

    private ValidationError toValidationError(FieldError fe) {
        return new ValidationError(fe.getField(), fe.getDefaultMessage());
    }

    public record ValidationError(String field, String message) {
        public String getMessage() { return message; }
    }
}