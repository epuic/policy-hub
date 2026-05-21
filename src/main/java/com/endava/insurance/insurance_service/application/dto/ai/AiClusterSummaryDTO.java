package com.endava.insurance.insurance_service.application.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Map;

public record AiClusterSummaryDTO(
        @JsonProperty("clusterId")
        Integer clusterId,
        @JsonProperty("label")
        String label,
        @JsonProperty("size")
        long size,
        @JsonProperty("adjustmentPercentage")
        BigDecimal adjustmentPercentage,
        @JsonProperty("numericAverages")
        Map<String, BigDecimal> numericAverages,
        @JsonProperty("categoricalModes")
        Map<String, String> categoricalModes
) {
}
