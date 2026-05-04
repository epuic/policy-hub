package com.endava.insurance.insurance_service.application.validator.client.strategy;

import com.endava.insurance.insurance_service.application.validator.client.strategy.country.DeIdentificationNumberFormatStrategy;
import com.endava.insurance.insurance_service.domain.enums.ClientType;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("DeIdentificationNumberFormatStrategy (DE)")
class DeIdentificationNumberFormatStrategyTest {

    private final DeIdentificationNumberFormatStrategy strategy = new DeIdentificationNumberFormatStrategy();

    @Nested
    @DisplayName("INDIVIDUAL (Steuer-ID)")
    class IndividualSteuerId {

        @Test
        @DisplayName("valid 11-digit Steuer-ID does not throw")
        void validSteuerId_passes() {
            assertThatCode(() -> strategy.validate(ClientType.INDIVIDUAL, "12345678901"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("10 digits is invalid")
        void tenDigits_throws() {
            assertThatThrownBy(() -> strategy.validate(ClientType.INDIVIDUAL, "1234567890"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Steuer-ID");
        }

        @Test
        @DisplayName("12 digits is invalid")
        void twelveDigits_throws() {
            assertThatThrownBy(() -> strategy.validate(ClientType.INDIVIDUAL, "123456789012"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Steuer-ID");
        }

        @Test
        @DisplayName("letters in Steuer-ID is invalid")
        void letters_throws() {
            assertThatThrownBy(() -> strategy.validate(ClientType.INDIVIDUAL, "1234567890a"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Steuer-ID");
        }
    }

    @Nested
    @DisplayName("COMPANY (Handelsregisternummer)")
    class CompanyHandelsregister {

        @Test
        @DisplayName("valid 5-char alphanumeric passes")
        void validFiveChar_passes() {
            assertThatCode(() -> strategy.validate(ClientType.COMPANY, "HRA12"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("valid 15-char alphanumeric passes")
        void validFifteenChar_passes() {
            assertThatCode(() -> strategy.validate(ClientType.COMPANY, "HRB247469B"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("4 chars is invalid")
        void fourChars_throws() {
            assertThatThrownBy(() -> strategy.validate(ClientType.COMPANY, "HRA1"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("DE COMPANY");
        }

        @Test
        @DisplayName("16 chars is invalid")
        void sixteenChars_throws() {
            assertThatThrownBy(() -> strategy.validate(ClientType.COMPANY, "HRB247469B123456"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("DE COMPANY");
        }
    }

    @Test
    @DisplayName("getCountryCode returns DE")
    void getCountryCode_returnsDe() {
        assertThat(strategy.getCountryCode()).isEqualTo("DE");
    }
}
