package com.endava.insurance.insurance_service.application.validator.metadata.secondary;

import com.endava.insurance.insurance_service.domain.enums.PolicyStatus;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.persistence.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrencyUsageValidator {

    private final PolicyRepository policyRepository;

    public void ensureNotUsedInActivePolicies(Long currencyId) throws ValidationException {
        if (policyRepository.existsByCurrencyIdAndStatus(currencyId, PolicyStatus.ACTIVE)) {
            throw new ValidationException("Cannot deactivate currency that is used in active policies");
        }
    }
}
