package com.endava.insurance.insurance_service.application.dto.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record CurrencyResponseDTO(
        @JsonProperty("id")
        Long id,
        @JsonProperty("code")
        String code,
        @JsonProperty("name")
        String name,
        @JsonProperty("exchangeRateToBase")
        BigDecimal exchangeRateToBase,
        @JsonProperty("active")
        boolean active
) {}
