package com.endava.insurance.insurance_service.application.validator.metadata.secondary;

import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.persistence.repository.RiskFactorConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RiskFactorConfigurationExistenceValidator {

    private final RiskFactorConfigurationRepository riskFactorConfigurationRepository;

    public void ensureRiskFactorConfigurationExists(Long riskFactorConfigurationId) throws ResourceNotFoundException {
        if (!riskFactorConfigurationRepository.existsById(riskFactorConfigurationId)) {
            throw new ResourceNotFoundException("Risk factor configuration not found with id: " + riskFactorConfigurationId);
        }
    }
}
