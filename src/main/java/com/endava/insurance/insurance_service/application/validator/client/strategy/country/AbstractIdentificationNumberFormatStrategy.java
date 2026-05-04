package com.endava.insurance.insurance_service.application.validator.client.strategy.country;

import com.endava.insurance.insurance_service.domain.enums.ClientType;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;

public abstract class AbstractIdentificationNumberFormatStrategy implements IdentificationNumberFormatStrategy {

    private static final String CLIENT_TYPE_REQUIRED_MSG = "Client type is required to validate identification number";
    private static final String IDENTIFICATION_NUMBER_REQUIRED_MSG = "Identification number is required";

    @Override
    public void validate(ClientType type, String identificationNumber) throws ValidationException {
        if (type == null) {
            throw new ValidationException(CLIENT_TYPE_REQUIRED_MSG);
        }
        if (identificationNumber == null || identificationNumber.isBlank()) {
            throw new ValidationException(IDENTIFICATION_NUMBER_REQUIRED_MSG);
        }
        String trimmed = identificationNumber.trim();

        if (type == ClientType.INDIVIDUAL) {
            validateIndividual(trimmed);
        } else {
            validateCompany(trimmed);
        }
    }

    protected abstract void validateIndividual(String trimmed) throws ValidationException;

    protected abstract void validateCompany(String trimmed) throws ValidationException;
}
