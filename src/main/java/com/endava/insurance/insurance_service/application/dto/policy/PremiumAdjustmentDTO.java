package com.endava.insurance.insurance_service.application.dto.policy;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record PremiumAdjustmentDTO(
        @JsonProperty("category")
        String category,

        @JsonProperty("label")
        String label,

        @JsonProperty("percentage")
        BigDecimal percentage,

        @JsonProperty("amount")
        BigDecimal amount
) {
}
