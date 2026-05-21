package com.endava.insurance.insurance_service.application.dto.ai;

import java.math.BigDecimal;

public record AiPythonAssignment(
        Long entityId,
        Integer clusterId,
        BigDecimal distance
) {
}
