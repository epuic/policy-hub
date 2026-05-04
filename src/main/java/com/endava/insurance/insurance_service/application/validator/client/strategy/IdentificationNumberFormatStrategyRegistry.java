package com.endava.insurance.insurance_service.application.validator.client.strategy;

import com.endava.insurance.insurance_service.application.validator.client.strategy.country.IdentificationNumberFormatStrategy;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IdentificationNumberFormatStrategyRegistry {

    private final List<IdentificationNumberFormatStrategy> strategies;

    private Map<String, IdentificationNumberFormatStrategy> strategyByCountry;

    private Map<String, IdentificationNumberFormatStrategy> getStrategyByCountry() {
        if (strategyByCountry == null) {
            strategyByCountry = strategies.stream()
                    .collect(Collectors.toMap(s -> s.getCountryCode().toUpperCase(), Function.identity(), (a, b) -> a));
        }
        return strategyByCountry;
    }

    public IdentificationNumberFormatStrategy getStrategy(String countryCode) throws ValidationException {
        if (countryCode == null || countryCode.isBlank()) {
            throw new ValidationException("Country code is required to validate identification number");
        }
        String key = countryCode.trim().toUpperCase();
        IdentificationNumberFormatStrategy strategy = getStrategyByCountry().get(key);
        if (strategy == null) {
            throw new ValidationException("Unsupported country code for identification number: " + countryCode
                    + ". Supported: " + String.join(", ", getStrategyByCountry().keySet()));
        }
        return strategy;
    }
}
