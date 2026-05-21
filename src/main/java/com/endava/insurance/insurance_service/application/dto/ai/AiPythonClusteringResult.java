package com.endava.insurance.insurance_service.application.dto.ai;

import com.endava.insurance.insurance_service.domain.enums.AiClusterTarget;

import java.util.List;

public record AiPythonClusteringResult(
        AiClusterTarget target,
        String algorithm,
        List<AiPythonAssignment> assignments,
        List<AiPythonClusterSummary> clusters
) {
}
