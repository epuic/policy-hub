package com.endava.insurance.insurance_service.application.dto.metadata;

import com.endava.insurance.insurance_service.domain.enums.FeeType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FeeConfigurationResponseDTO(
        @JsonProperty("id")
        Long id,
        @JsonProperty("name")
        String name,
        @JsonProperty("type")
        FeeType type,
        @JsonProperty("percentage")
        BigDecimal percentage,
        @JsonProperty("effectiveFrom")
        LocalDate effectiveFrom,
        @JsonProperty("effectiveTo")
        LocalDate effectiveTo,
        @JsonProperty("active")
        boolean active
) {}
