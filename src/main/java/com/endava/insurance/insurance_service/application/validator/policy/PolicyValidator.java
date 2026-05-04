package com.endava.insurance.insurance_service.application.validator.policy;

import com.endava.insurance.insurance_service.application.dto.policy.PolicyCreateDTO;
import com.endava.insurance.insurance_service.application.validator.policy.secondary.PolicyBusinessValidator;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PolicyValidator {

    private final PolicyBusinessValidator policyBusinessValidator;


    public void validateNewPolicy(PolicyCreateDTO request) throws ResourceNotFoundException, ValidationException {
        policyBusinessValidator.ensureClientExists(request.clientId());
        policyBusinessValidator.ensureBuildingExists(request.buildingId());
        policyBusinessValidator.ensureBuildingBelongsToClient(request.buildingId(), request.clientId());
        policyBusinessValidator.ensureBrokerExists(request.brokerId());
        policyBusinessValidator.ensureBrokerIsActive(request.brokerId());
        policyBusinessValidator.ensureCurrencyExists(request.currencyId());
        policyBusinessValidator.ensureCurrencyIsActive(request.currencyId());
    }
}
