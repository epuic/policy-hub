package com.endava.insurance.insurance_service.application.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AiClusterConfigurationRequestDTO(
        @NotBlank(message = "Label is required")
        @JsonProperty("label")
        String label,

        @NotNull(message = "Adjustment percentage is required")
        @DecimalMin(value = "-100", message = "Adjustment percentage must be at least -100")
        @DecimalMax(value = "100", message = "Adjustment percentage must not exceed 100")
        @JsonProperty("adjustmentPercentage")
        BigDecimal adjustmentPercentage,

        @JsonProperty("active")
        boolean active
) {
}
