package com.endava.insurance.insurance_service.application.validator.metadata;

import com.endava.insurance.insurance_service.application.dto.metadata.CurrencyRequestDTO;
import com.endava.insurance.insurance_service.application.validator.metadata.secondary.CurrencyUniquenessValidator;
import com.endava.insurance.insurance_service.application.validator.metadata.secondary.CurrencyUsageValidator;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.metadata.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrencyValidator {

    private final CurrencyUniquenessValidator currencyUniquenessValidator;
    private final CurrencyUsageValidator currencyUsageValidator;

    public void validateNewCurrency(CurrencyRequestDTO request) throws ValidationException {
        currencyUniquenessValidator.ensureCodeUnique(request.code());
    }

    public void validateCurrencyDeactivation(Currency currency) throws ValidationException {
        currencyUsageValidator.ensureNotUsedInActivePolicies(currency.getId());
    }

    public void validateCurrencyUpdate(Currency existing, CurrencyRequestDTO request) throws ValidationException {
        String requestCode = request.code() != null ? request.code().trim().toUpperCase() : "";
        String existingCode = existing.getCode() != null ? existing.getCode() : "";
        if (!existingCode.equals(requestCode)) {
            throw new ValidationException("Currency code cannot be changed. Expected: " + existingCode + ", received: " + requestCode);
        }
    }
}
