package com.endava.insurance.insurance_service.application.dto.ai;

import java.math.BigDecimal;
import java.util.Map;

public record AiFeatureRecord(
        Long id,
        Map<String, BigDecimal> numeric,
        Map<String, String> categorical
) {
}
