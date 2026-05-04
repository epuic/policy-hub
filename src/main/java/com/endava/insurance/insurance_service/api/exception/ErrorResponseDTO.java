package com.endava.insurance.insurance_service.api.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseDTO(
        @JsonProperty("timestamp")
        LocalDateTime timestamp,
        @JsonProperty("status")
        int status,
        @JsonProperty("error")
        String error,
        @JsonProperty("message")
        String message,
        @JsonProperty("details")
        List<String> details
) {
}
