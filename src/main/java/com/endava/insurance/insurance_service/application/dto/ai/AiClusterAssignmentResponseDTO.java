package com.endava.insurance.insurance_service.application.dto.ai;

import com.endava.insurance.insurance_service.domain.enums.AiClusterTarget;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AiClusterAssignmentResponseDTO(
        @JsonProperty("target")
        AiClusterTarget target,
        @JsonProperty("entityId")
        Long entityId,
        @JsonProperty("clusterId")
        Integer clusterId,
        @JsonProperty("clusterLabel")
        String clusterLabel,
        @JsonProperty("distance")
        BigDecimal distance,
        @JsonProperty("algorithm")
        String algorithm,
        @JsonProperty("runAt")
        LocalDateTime runAt
) {
}
