package com.endava.insurance.insurance_service.application.validator.client.strategy;

import com.endava.insurance.insurance_service.application.validator.client.strategy.country.RoIdentificationNumberFormatStrategy;
import com.endava.insurance.insurance_service.domain.enums.ClientType;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("RoIdentificationNumberFormatStrategy (RO)")
class RoIdentificationNumberFormatStrategyTest {

    private final RoIdentificationNumberFormatStrategy strategy = new RoIdentificationNumberFormatStrategy();

    @Nested
    @DisplayName("INDIVIDUAL (CNP)")
    class IndividualCnp {

        @Test
        @DisplayName("valid 13-digit CNP does not throw")
        void validCnp_passes() {
            assertThatCode(() -> strategy.validate(ClientType.INDIVIDUAL, "1234567890123"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("trimmed valid CNP passes")
        void trimmedValidCnp_passes() {
            assertThatCode(() -> strategy.validate(ClientType.INDIVIDUAL, "  1234567890123  "))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("CNP starting with 0 is invalid")
        void cnpStartingWithZero_throws() {
            assertThatThrownBy(() -> strategy.validate(ClientType.INDIVIDUAL, "0234567890123"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("valid CNP");
        }

        @Test
        @DisplayName("CNP with 12 digits is invalid")
        void cnpTooShort_throws() {
            assertThatThrownBy(() -> strategy.validate(ClientType.INDIVIDUAL, "123456789012"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("valid CNP");
        }

        @Test
        @DisplayName("CNP with letters is invalid")
        void cnpWithLetters_throws() {
            assertThatThrownBy(() -> strategy.validate(ClientType.INDIVIDUAL, "123456789012a"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("valid CNP");
        }
    }

    @Nested
    @DisplayName("COMPANY (CUI)")
    class CompanyCui {

        @Test
        @DisplayName("valid 2-digit CUI passes")
        void validCuiTwoDigits_passes() {
            assertThatCode(() -> strategy.validate(ClientType.COMPANY, "12"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("valid 10-digit CUI passes")
        void validCuiTenDigits_passes() {
            assertThatCode(() -> strategy.validate(ClientType.COMPANY, "1234567890"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("CUI with 1 digit is invalid")
        void cuiOneDigit_throws() {
            assertThatThrownBy(() -> strategy.validate(ClientType.COMPANY, "1"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("valid CUI");
        }

        @Test
        @DisplayName("CUI with letters is invalid")
        void cuiWithLetters_throws() {
            assertThatThrownBy(() -> strategy.validate(ClientType.COMPANY, "12345678a"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("valid CUI");
        }
    }

    @Nested
    @DisplayName("Input validation")
    class InputValidation {

        @Test
        @DisplayName("null type throws")
        void nullType_throws() {
            assertThatThrownBy(() -> strategy.validate(null, "1234567890123"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Client type is required");
        }

        @Test
        @DisplayName("null identification number throws")
        void nullIdentificationNumber_throws() {
            assertThatThrownBy(() -> strategy.validate(ClientType.INDIVIDUAL, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Identification number is required");
        }

        @Test
        @DisplayName("blank identification number throws")
        void blankIdentificationNumber_throws() {
            assertThatThrownBy(() -> strategy.validate(ClientType.INDIVIDUAL, "   "))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Identification number is required");
        }
    }

    @Test
    @DisplayName("getCountryCode returns RO")
    void getCountryCode_returnsRo() {
        assertThat(strategy.getCountryCode()).isEqualTo("RO");
    }
}
