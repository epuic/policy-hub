package com.endava.insurance.insurance_service.application.validator.client.strategy.country;

import com.endava.insurance.insurance_service.domain.enums.ClientType;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;

public interface IdentificationNumberFormatStrategy {

    String getCountryCode();

    void validate(ClientType type, String identificationNumber) throws ValidationException;
}
