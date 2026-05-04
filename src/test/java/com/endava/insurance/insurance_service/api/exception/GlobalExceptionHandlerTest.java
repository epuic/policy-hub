package com.endava.insurance.insurance_service.api.exception;

import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("handleNotFound returns 404 and Not Found message")
    void handleNotFound_returns404() {
        var ex = new ResourceNotFoundException("Client not found with id: 999");
        ResponseEntity<ErrorResponseDTO> res = handler.handleNotFound(ex);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().status()).isEqualTo(404);
        assertThat(res.getBody().error()).isEqualTo("Not Found");
        assertThat(res.getBody().message()).isEqualTo("Client not found with id: 999");
        assertThat(res.getBody().details()).isNull();
    }

    @Test
    @DisplayName("handleValidation returns 400 and Validation Error message")
    void handleValidation_returns400() {
        var ex = new ValidationException("Email already in use");
        ResponseEntity<ErrorResponseDTO> res = handler.handleValidation(ex);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().status()).isEqualTo(400);
        assertThat(res.getBody().error()).isEqualTo("Validation Error");
        assertThat(res.getBody().message()).isEqualTo("Email already in use");
        assertThat(res.getBody().details()).isNull();
    }

    @Test
    @DisplayName("handleMethodArgumentNotValid returns 400 with field details")
    void handleMethodArgumentNotValid_returns400WithDetails() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("request", "name", "must not be blank"),
                new FieldError("request", "email", "must be a valid email")
        ));
        var ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponseDTO> res = handler.handleMethodArgumentNotValid(ex);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().status()).isEqualTo(400);
        assertThat(res.getBody().error()).isEqualTo("Validation Failed");
        assertThat(res.getBody().details()).hasSize(2);
        assertThat(res.getBody().details()).anyMatch(s -> s.contains("name") && s.contains("must not be blank"));
        assertThat(res.getBody().details()).anyMatch(s -> s.contains("email") && s.contains("must be a valid email"));
    }

    @Test
    @DisplayName("handleHttpMessageNotReadable returns 400")
    void handleHttpMessageNotReadable_returns400() {
        var ex = mock(org.springframework.http.converter.HttpMessageNotReadableException.class);
        when(ex.getMessage()).thenReturn("Invalid JSON");
        ResponseEntity<ErrorResponseDTO> res = handler.handleHttpMessageNotReadable(ex);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().status()).isEqualTo(400);
        assertThat(res.getBody().error()).isEqualTo("Bad Request");
        assertThat(res.getBody().message()).isEqualTo("Invalid request body. Check JSON format and field types.");
    }

    @Test
    @DisplayName("handleTypeMismatch returns 400 with parameter name and expected type")
    void handleTypeMismatch_returns400() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("id");
        when(ex.getValue()).thenReturn("abc");
        when(ex.getRequiredType()).thenReturn((Class) Long.class);

        ResponseEntity<ErrorResponseDTO> res = handler.handleTypeMismatch(ex);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().status()).isEqualTo(400);
        assertThat(res.getBody().error()).isEqualTo("Bad Request");
        assertThat(res.getBody().message()).contains("id").contains("Long");
    }

    @Test
    @DisplayName("handleTypeMismatch when requiredType is null mentions unknown")
    void handleTypeMismatch_nullRequiredType_usesUnknown() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("x");
        when(ex.getValue()).thenReturn("y");
        when(ex.getRequiredType()).thenReturn(null);

        ResponseEntity<ErrorResponseDTO> res = handler.handleTypeMismatch(ex);

        assertThat(res.getBody().message()).contains("unknown");
    }

    @Test
    @DisplayName("handleUnexpected returns 500")
    void handleUnexpected_returns500() {
        var ex = new RuntimeException("Something broke");
        ResponseEntity<ErrorResponseDTO> res = handler.handleUnexpected(ex);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().status()).isEqualTo(500);
        assertThat(res.getBody().error()).isEqualTo("Internal Server Error");
        assertThat(res.getBody().message()).isEqualTo("An unexpected error occurred. Please try again later.");
    }
}
