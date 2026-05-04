package com.endava.insurance.insurance_service.application.validator.client.strategy;

import com.endava.insurance.insurance_service.application.validator.client.strategy.country.DeIdentificationNumberFormatStrategy;
import com.endava.insurance.insurance_service.application.validator.client.strategy.country.RoIdentificationNumberFormatStrategy;
import com.endava.insurance.insurance_service.domain.enums.ClientType;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("IdentificationNumberFormatStrategyRegistry")
class IdentificationNumberFormatStrategyRegistryTest {

    private final RoIdentificationNumberFormatStrategy roStrategy = new RoIdentificationNumberFormatStrategy();
    private final DeIdentificationNumberFormatStrategy deStrategy = new DeIdentificationNumberFormatStrategy();
    private final IdentificationNumberFormatStrategyRegistry registry =
            new IdentificationNumberFormatStrategyRegistry(List.of(roStrategy, deStrategy));

    @Test
    @DisplayName("getStrategy for RO returns RO strategy")
    void getStrategy_ro_returnsRoStrategy() throws ValidationException {
        var strategy = registry.getStrategy("RO");
        assertThat(strategy.getCountryCode()).isEqualTo("RO");
        assertThatCode(() -> strategy.validate(ClientType.INDIVIDUAL, "1234567890123")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("getStrategy for ro (lowercase) returns RO strategy")
    void getStrategy_roLowercase_returnsRoStrategy() throws ValidationException {
        var strategy = registry.getStrategy("ro");
        assertThat(strategy.getCountryCode()).isEqualTo("RO");
    }

    @Test
    @DisplayName("getStrategy for DE returns DE strategy")
    void getStrategy_de_returnsDeStrategy() throws ValidationException {
        var strategy = registry.getStrategy("DE");
        assertThat(strategy.getCountryCode()).isEqualTo("DE");
    }

    @Test
    @DisplayName("getStrategy for unsupported country throws")
    void getStrategy_unsupported_throws() {
        assertThatThrownBy(() -> registry.getStrategy("XX"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Unsupported country code")
                .hasMessageContaining("XX");
    }

    @Test
    @DisplayName("getStrategy for null country code throws")
    void getStrategy_null_throws() {
        assertThatThrownBy(() -> registry.getStrategy(null))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Country code is required");
    }

    @Test
    @DisplayName("getStrategy for blank country code throws")
    void getStrategy_blank_throws() {
        assertThatThrownBy(() -> registry.getStrategy("   "))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Country code is required");
    }
}
