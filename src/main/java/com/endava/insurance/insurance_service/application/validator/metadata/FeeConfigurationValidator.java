package com.endava.insurance.insurance_service.application.validator.metadata;

import com.endava.insurance.insurance_service.application.dto.metadata.FeeConfigurationRequestDTO;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeeConfigurationValidator {

    public void validateNewFeeConfiguration(FeeConfigurationRequestDTO request) throws ValidationException {
        validateEffectiveDateRange(request);
    }

    public void validateFeeConfigurationUpdate(FeeConfigurationRequestDTO request) throws ValidationException {
        validateEffectiveDateRange(request);
    }

    private void validateEffectiveDateRange(FeeConfigurationRequestDTO request) throws ValidationException {
        if (request.effectiveFrom() != null && request.effectiveTo() != null && request.effectiveTo().isBefore(request.effectiveFrom())) {
            throw new ValidationException("EffectiveTo date must be on or after EffectiveFrom date");
        }
    }
}
