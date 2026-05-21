package com.endava.insurance.insurance_service.application.dto.ai;

import com.endava.insurance.insurance_service.domain.enums.AiClusterTarget;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record AiClusterConfigurationResponseDTO(
        @JsonProperty("id")
        Long id,
        @JsonProperty("target")
        AiClusterTarget target,
        @JsonProperty("clusterId")
        Integer clusterId,
        @JsonProperty("label")
        String label,
        @JsonProperty("adjustmentPercentage")
        BigDecimal adjustmentPercentage,
        @JsonProperty("active")
        boolean active
) {
}
