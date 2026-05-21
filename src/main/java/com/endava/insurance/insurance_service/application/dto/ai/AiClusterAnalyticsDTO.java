package com.endava.insurance.insurance_service.application.dto.ai;

import com.endava.insurance.insurance_service.domain.enums.AiClusterTarget;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record AiClusterAnalyticsDTO(
        @JsonProperty("target")
        AiClusterTarget target,

        @JsonProperty("clusterId")
        Integer clusterId,

        @JsonProperty("label")
        String label,

        @JsonProperty("size")
        int size,

        @JsonProperty("numericAverages")
        Map<String, BigDecimal> numericAverages,

        @JsonProperty("categoricalModes")
        Map<String, String> categoricalModes,

        @JsonProperty("members")
        List<AiClusterMemberDTO> members
) {
}
