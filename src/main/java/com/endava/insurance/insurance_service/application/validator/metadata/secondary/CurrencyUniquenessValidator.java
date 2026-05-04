package com.endava.insurance.insurance_service.application.validator.metadata.secondary;

import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.persistence.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrencyUniquenessValidator {

    private final CurrencyRepository currencyRepository;

    public void ensureCodeUnique(String code) throws ValidationException {
        if (currencyRepository.existsByCode(code)) {
            throw new ValidationException("Currency code is already in use: " + code);
        }
    }
}
