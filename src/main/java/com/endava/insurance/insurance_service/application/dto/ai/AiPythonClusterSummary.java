package com.endava.insurance.insurance_service.application.dto.ai;

import java.math.BigDecimal;
import java.util.Map;

public record AiPythonClusterSummary(
        Integer clusterId,
        String label,
        long size,
        Map<String, BigDecimal> numericAverages,
        Map<String, String> categoricalModes
) {
}
