package com.endava.insurance.insurance_service.application.dto.metadata;

import com.endava.insurance.insurance_service.domain.enums.RiskFactorConfigLevel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = false)
public record RiskFactorConfigurationRequestDTO(
        @NotNull(message = "Level is required")
        @JsonProperty("level")
        RiskFactorConfigLevel level,

        @JsonProperty("referenceId")
        String referenceId,

        @NotNull(message = "Adjustment percentage is required")
        @DecimalMin(value = "-100", message = "Adjustment percentage must be at least -100")
        @DecimalMax(value = "100", message = "Adjustment percentage must be at most 100")
        @JsonProperty("adjustmentPercentage")
        BigDecimal adjustmentPercentage,

        @JsonProperty("active")
        boolean active
) {}
