package com.endava.insurance.insurance_service.application.validator.client.strategy.country;

import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class DeIdentificationNumberFormatStrategy extends AbstractIdentificationNumberFormatStrategy {

    private static final String COUNTRY_CODE = "DE";
    private static final Pattern STEUER_ID_PATTERN = Pattern.compile("^\\d{11}$");
    private static final Pattern HANDELSREGISTER_PATTERN = Pattern.compile("^[A-Za-z0-9]{5,15}$");

    @Override
    public String getCountryCode() {
        return COUNTRY_CODE;
    }

    @Override
    protected void validateIndividual(String trimmed) throws ValidationException {
        if (!STEUER_ID_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException(
                    "For DE INDIVIDUAL client, identification number must be a valid Steuer-ID (11 digits)");
        }
    }

    @Override
    protected void validateCompany(String trimmed) throws ValidationException {
        if (!HANDELSREGISTER_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException(
                    "For DE COMPANY client, identification number must be valid (e.g. Handelsregisternummer, 5-15 alphanumeric)");
        }
    }
}
