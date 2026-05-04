package com.endava.insurance.insurance_service.application.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email is required")
        @JsonProperty("email")
        String email,

        @NotBlank(message = "Password is required")
        @JsonProperty("password")
        String password
) {}
