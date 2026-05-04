package com.endava.insurance.insurance_service.application.validator.metadata;

import com.endava.insurance.insurance_service.application.dto.metadata.CurrencyRequestDTO;
import com.endava.insurance.insurance_service.application.validator.metadata.secondary.CurrencyUniquenessValidator;
import com.endava.insurance.insurance_service.application.validator.metadata.secondary.CurrencyUsageValidator;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.metadata.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CurrencyValidator")
@SuppressWarnings("java:S1130")
class CurrencyValidatorTest {

    @Mock
    private CurrencyUniquenessValidator currencyUniquenessValidator;

    @Mock
    private CurrencyUsageValidator currencyUsageValidator;

    @InjectMocks
    private CurrencyValidator validator;

    @Test
    @DisplayName("validateNewCurrency delegates to uniqueness and does not throw")
    void validateNewCurrency_delegates_doesNotThrow() throws ValidationException {
        CurrencyRequestDTO request = new CurrencyRequestDTO("EUR", "Euro", BigDecimal.ONE, true);
        doNothing().when(currencyUniquenessValidator).ensureCodeUnique("EUR");

        assertThatCode(() -> validator.validateNewCurrency(request)).doesNotThrowAnyException();
        verify(currencyUniquenessValidator).ensureCodeUnique("EUR");
    }

    @ParameterizedTest(name = "existingCode={0}, requestCode={1}, expectThrow={2}")
    @CsvSource(value = {
            "RON, RON, false",
            "RON, EUR, true",
            "null, null, false"
    }, nullValues = "null")
    @DisplayName("validateCurrencyUpdate various code scenarios")
    void validateCurrencyUpdate_variousCodes(String existingCode, String requestCode, boolean expectThrow) throws ValidationException {
        Currency existing = mock(Currency.class);
        when(existing.getCode()).thenReturn(existingCode);
        CurrencyRequestDTO request = new CurrencyRequestDTO(requestCode, "Name", BigDecimal.ONE, true);

        if (expectThrow) {
            assertThatThrownBy(() -> validator.validateCurrencyUpdate(existing, request))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Currency code cannot be changed");
        } else {
            assertThatCode(() -> validator.validateCurrencyUpdate(existing, request)).doesNotThrowAnyException();
        }
    }

    @Test
    @DisplayName("validateCurrencyDeactivation delegates to usage validator")
    void validateCurrencyDeactivation_delegates() throws ValidationException {
        Currency currency = mock(Currency.class);
        when(currency.getId()).thenReturn(1L);
        doNothing().when(currencyUsageValidator).ensureNotUsedInActivePolicies(1L);

        assertThatCode(() -> validator.validateCurrencyDeactivation(currency)).doesNotThrowAnyException();
        verify(currencyUsageValidator).ensureNotUsedInActivePolicies(1L);
    }

    @Test
    @DisplayName("validateCurrencyUpdate when request code blank trimmed – compares as empty")
    void validateCurrencyUpdate_requestCodeBlank_comparesAsEmpty() throws ValidationException {
        Currency existing = mock(Currency.class);
        when(existing.getCode()).thenReturn(null);
        CurrencyRequestDTO request = new CurrencyRequestDTO("  ", "Name", BigDecimal.ONE, true);

        assertThatCode(() -> validator.validateCurrencyUpdate(existing, request)).doesNotThrowAnyException();
    }
}
