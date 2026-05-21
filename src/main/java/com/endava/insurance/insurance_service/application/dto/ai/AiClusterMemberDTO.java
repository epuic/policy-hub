package com.endava.insurance.insurance_service.application.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Map;

public record AiClusterMemberDTO(
        @JsonProperty("entityId")
        Long entityId,

        @JsonProperty("name")
        String name,

        @JsonProperty("subtitle")
        String subtitle,

        @JsonProperty("metrics")
        Map<String, BigDecimal> metrics
) {
}
