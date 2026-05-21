package com.endava.insurance.insurance_service.application.dto.ai;

import com.endava.insurance.insurance_service.domain.enums.AiClusterTarget;

import java.util.List;

public record AiClusteringPayload(
        AiClusterTarget target,
        int k,
        int maxIterations,
        List<AiFeatureRecord> records
) {
}
