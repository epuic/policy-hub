package com.endava.insurance.insurance_service.application.validator.metadata.secondary;

import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.persistence.repository.CurrencyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CurrencyUniquenessValidator")
@SuppressWarnings("java:S1130")
class CurrencyUniquenessValidatorTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private CurrencyUniquenessValidator validator;

    @Test
    @DisplayName("ensureCodeUnique when code not exists – does not throw")
    void whenCodeUnique_doesNotThrow() throws ValidationException {
        when(currencyRepository.existsByCode("EUR")).thenReturn(false);
        assertThatCode(() -> validator.ensureCodeUnique("EUR")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("ensureCodeUnique when code exists – throws ValidationException")
    void whenCodeExists_throws() {
        when(currencyRepository.existsByCode("RON")).thenReturn(true);
        assertThatThrownBy(() -> validator.ensureCodeUnique("RON"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Currency code is already in use");
    }
}
