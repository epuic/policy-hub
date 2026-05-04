package com.endava.insurance.insurance_service.api.exception;

import com.endava.insurance.insurance_service.domain.exception.AccountInactiveException;
import com.endava.insurance.insurance_service.domain.exception.InvalidCredentialsException;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidCredentials(InvalidCredentialsException ex) {
        log.warn("Failure - invalid credentials: {}", ex.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(ZoneOffset.UTC),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccountInactiveException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccountInactive(AccountInactiveException ex) {
        log.warn("Failure - account inactive: {}", ex.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(ZoneOffset.UTC),
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Failure - resource not found: {}", ex.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(ZoneOffset.UTC),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(ValidationException ex) {
        log.warn("Failure - validation error: {}", ex.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(ZoneOffset.UTC),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(ZoneOffset.UTC),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "Required fields are missing or invalid. Check the details for each field.",
                details
        );
        log.warn("Failure - request validation failed: {}", details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Failure - invalid request body: {}", ex.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(ZoneOffset.UTC),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Invalid request body. Check JSON format and field types.",
                null
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Failure - invalid parameter: {} = {}", ex.getName(), ex.getValue());
        String message = String.format("Invalid value for '%s': expected type %s.", ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(ZoneOffset.UTC),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                message,
                null
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleUnexpected(Exception ex) {
        log.error("Failure - unexpected error: {}", ex.getMessage(), ex);
        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(ZoneOffset.UTC),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                null
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}