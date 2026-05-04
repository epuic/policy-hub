package com.endava.insurance.insurance_service.application.dto.metadata;

import com.endava.insurance.insurance_service.domain.enums.RiskFactorConfigLevel;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record RiskFactorConfigurationResponseDTO(
        @JsonProperty("id")
        Long id,
        @JsonProperty("level")
        RiskFactorConfigLevel level,
        @JsonProperty("referenceId")
        String referenceId,
        @JsonProperty("referenceName")
        String referenceName,
        @JsonProperty("adjustmentPercentage")
        BigDecimal adjustmentPercentage,
        @JsonProperty("active")
        boolean active
) {}
