package com.endava.insurance.insurance_service.application.validator.client.strategy.country;

import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class RoIdentificationNumberFormatStrategy extends AbstractIdentificationNumberFormatStrategy {

    private static final String COUNTRY_CODE = "RO";
    private static final Pattern CNP_PATTERN = Pattern.compile("^[1-9]\\d{12}$");
    private static final Pattern CUI_PATTERN = Pattern.compile("^\\d{2,10}$");

    @Override
    public String getCountryCode() {
        return COUNTRY_CODE;
    }

    @Override
    protected void validateIndividual(String trimmed) throws ValidationException {
        if (!CNP_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException(
                    "For RO INDIVIDUAL client, identification number must be a valid CNP (13 digits, first digit 1-9)");
        }
    }

    @Override
    protected void validateCompany(String trimmed) throws ValidationException {
        if (!CUI_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException(
                    "For RO COMPANY client, identification number must be a valid CUI (2-10 digits)");
        }
    }
}
