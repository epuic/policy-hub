package com.endava.insurance.insurance_service.application.dto.report;

import com.endava.insurance.insurance_service.domain.enums.BuildingType;
import com.endava.insurance.insurance_service.domain.enums.PolicyStatus;

import java.time.LocalDate;

public record PolicyReportFilter(
        LocalDate from,
        LocalDate to,
        PolicyStatus status,
        String currencyCode,
        BuildingType buildingType
) {
}
