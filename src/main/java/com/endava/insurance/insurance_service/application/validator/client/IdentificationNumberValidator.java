package com.endava.insurance.insurance_service.application.validator.client;

import com.endava.insurance.insurance_service.application.validator.client.strategy.IdentificationNumberFormatStrategyRegistry;
import com.endava.insurance.insurance_service.domain.enums.ClientType;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.persistence.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdentificationNumberValidator {

    private final ClientRepository clientRepository;
    private final IdentificationNumberFormatStrategyRegistry strategyRegistry;

    public void validateFormat(String countryCode, ClientType type, String identificationNumber) throws ValidationException {
        strategyRegistry.getStrategy(countryCode).validate(type, identificationNumber);
    }

    public void validateUnique(String identificationNumber) throws ValidationException {
        if (clientRepository.existsByIdentificationNumber(identificationNumber)) {
            throw new ValidationException("Identification number already exists: " + identificationNumber);
        }
    }

    public void validateNotChanged(String existing, String requested) throws ValidationException {
        if (!existing.equals(requested)) {
            throw new ValidationException(
                    "Changing the primary identifier (CNP/CUI) requires a change request. Use the CNP change request endpoint.");
        }
    }
}
