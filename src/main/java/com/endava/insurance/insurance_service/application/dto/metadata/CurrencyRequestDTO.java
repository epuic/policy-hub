package com.endava.insurance.insurance_service.application.dto.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = false)
public record CurrencyRequestDTO(
        @NotBlank(message = "Currency code is required")
        @Size(min = 1, max = 10)
        @JsonProperty("code")
        String code,

        @NotBlank(message = "Currency name is required")
        @Size(max = 100)
        @JsonProperty("name")
        String name,

        @NotNull(message = "Exchange rate to base is required")
        @DecimalMin(value = "0.000001", message = "Exchange rate must be positive")
        @JsonProperty("exchangeRateToBase")
        BigDecimal exchangeRateToBase,

        @JsonProperty("active")
        boolean active
) {}
