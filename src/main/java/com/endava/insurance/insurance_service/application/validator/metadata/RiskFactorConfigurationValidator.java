package com.endava.insurance.insurance_service.application.validator.metadata;

import com.endava.insurance.insurance_service.application.dto.metadata.RiskFactorConfigurationRequestDTO;
import com.endava.insurance.insurance_service.application.validator.metadata.secondary.RiskFactorConfigurationExistenceValidator;
import com.endava.insurance.insurance_service.domain.enums.BuildingType;
import com.endava.insurance.insurance_service.domain.enums.RiskFactorConfigLevel;
import com.endava.insurance.insurance_service.domain.enums.RiskFactorType;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class RiskFactorConfigurationValidator {

    private final RiskFactorConfigurationExistenceValidator riskFactorConfigurationExistenceValidator;

    public void validateNewRiskFactorConfiguration(RiskFactorConfigurationRequestDTO request) throws ValidationException {
        validateReferenceIdForLevel(request.level(), request.referenceId());
        validateAdjustmentPercentage(request.adjustmentPercentage());
    }

    public void validateRiskFactorConfigurationUpdate(RiskFactorConfigurationRequestDTO request) throws ValidationException {
        validateNewRiskFactorConfiguration(request);
    }

    private void validateReferenceIdForLevel(RiskFactorConfigLevel level, String referenceId) throws ValidationException {
        if (level == null) {
            return;
        }
        switch (level) {
            case COUNTRY, COUNTY, CITY -> validateGeographyReferenceId(referenceId, level);
            case BUILDING_TYPE -> validateBuildingTypeReferenceId(referenceId);
            case RISK_FACTOR_TYPE -> validateRiskFactorTypeReferenceId(referenceId);
        }
    }

    private void validateGeographyReferenceId(String referenceId, RiskFactorConfigLevel level) throws ValidationException {
        requireReferenceIdPresent(referenceId, level);
        if (!referenceId.trim().matches("^\\d+$")) {
            throw new ValidationException("Reference ID must be a numeric ID for level " + level);
        }
    }

    private void validateBuildingTypeReferenceId(String referenceId) throws ValidationException {
        requireReferenceIdPresent(referenceId, RiskFactorConfigLevel.BUILDING_TYPE);
        try {
            BuildingType.valueOf(referenceId.trim());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid building type: " + referenceId + ". Valid values: " + Arrays.toString(BuildingType.values()));
        }
    }

    private void validateRiskFactorTypeReferenceId(String referenceId) throws ValidationException {
        requireReferenceIdPresent(referenceId, RiskFactorConfigLevel.RISK_FACTOR_TYPE);
        try {
            RiskFactorType.valueOf(referenceId.trim());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid risk factor type: " + referenceId + ". Valid values: " + Arrays.toString(RiskFactorType.values()));
        }
    }

    private void requireReferenceIdPresent(String referenceId, RiskFactorConfigLevel level) throws ValidationException {
        if (referenceId == null || referenceId.isBlank()) {
            throw new ValidationException("Reference ID is required for level " + level);
        }
    }

    private void validateAdjustmentPercentage(BigDecimal adjustmentPercentage) throws ValidationException {
        if (adjustmentPercentage == null) {
            return;
        }
        if (adjustmentPercentage.compareTo(new BigDecimal("-100")) < 0 || adjustmentPercentage.compareTo(new BigDecimal("100")) > 0) {
            throw new ValidationException("Adjustment percentage must be between -100 and 100");
        }
    }
}
