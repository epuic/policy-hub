package com.endava.insurance.insurance_service.application.dto.policy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = false)
public record PolicyCancelDTO(
        @NotBlank(message = "Cancellation reason is required")
        @Size(max = 1000, message = "Cancellation reason must be at most 1000 characters")
        @JsonProperty("reason")
        String reason
) {}
