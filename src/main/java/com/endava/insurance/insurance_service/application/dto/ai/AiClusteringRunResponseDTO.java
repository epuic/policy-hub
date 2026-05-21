package com.endava.insurance.insurance_service.application.dto.ai;

import com.endava.insurance.insurance_service.domain.enums.AiClusterTarget;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AiClusteringRunResponseDTO(
        @JsonProperty("target")
        AiClusterTarget target,
        @JsonProperty("algorithm")
        String algorithm,
        @JsonProperty("recordsProcessed")
        int recordsProcessed,
        @JsonProperty("clusterCount")
        int clusterCount,
        @JsonProperty("clusters")
        List<AiClusterSummaryDTO> clusters
) {
}
