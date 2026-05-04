package com.endava.insurance.insurance_service.application.validator.client;

import com.endava.insurance.insurance_service.application.validator.client.strategy.IdentificationNumberFormatStrategyRegistry;
import com.endava.insurance.insurance_service.application.validator.client.strategy.country.RoIdentificationNumberFormatStrategy;
import com.endava.insurance.insurance_service.domain.enums.ClientType;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.persistence.repository.ClientRepository;
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
@DisplayName("IdentificationNumberValidator - unit tests")
@SuppressWarnings("java:S1130")
class IdentificationNumberValidatorTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private IdentificationNumberFormatStrategyRegistry strategyRegistry;

    @InjectMocks
    private IdentificationNumberValidator validator;

    private final RoIdentificationNumberFormatStrategy roStrategy = new RoIdentificationNumberFormatStrategy();

    @Test
    @DisplayName("validateFormat delegates to strategy - invalid CNP throws")
    void validateFormat_invalidCnp_throws() throws ValidationException {
        when(strategyRegistry.getStrategy("RO")).thenReturn(roStrategy);
        assertThatThrownBy(() -> validator.validateFormat("RO", ClientType.INDIVIDUAL, "123"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("valid CNP");
    }

    @Test
    @DisplayName("validateFormat delegates to strategy - invalid CUI throws")
    void validateFormat_invalidCui_throws() throws ValidationException {
        when(strategyRegistry.getStrategy("RO")).thenReturn(roStrategy);
        assertThatThrownBy(() -> validator.validateFormat("RO", ClientType.COMPANY, "RO123"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("valid CUI");
    }

    @Test
    @DisplayName("validateFormat delegates to strategy - valid CNP does not throw")
    void validateFormat_validCnp_doesNotThrow() throws ValidationException {
        when(strategyRegistry.getStrategy("RO")).thenReturn(roStrategy);
        assertThatCode(() -> validator.validateFormat("RO", ClientType.INDIVIDUAL, "1234567890123"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("duplicate identification number should fail")
    void validateUnique_duplicateIdentificationNumber_throws() throws ValidationException {
        String existingCnp = "1234567890123";
        when(clientRepository.existsByIdentificationNumber(existingCnp)).thenReturn(true);

        assertThatThrownBy(() -> validator.validateUnique(existingCnp))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Identification number already exists")
                .hasMessageContaining(existingCnp);
    }

    @Test
    @DisplayName("unique identification number should pass")
    void validateUnique_uniqueIdentificationNumber_doesNotThrow() throws ValidationException, ResourceNotFoundException {
        String cnp = "1234567890123";
        when(clientRepository.existsByIdentificationNumber(cnp)).thenReturn(false);
        when(strategyRegistry.getStrategy("RO")).thenReturn(roStrategy);

        validator.validateFormat("RO", ClientType.INDIVIDUAL, cnp);
        validator.validateUnique(cnp);
    }

    @Test
    @DisplayName("validateNotChanged when same identifier does not throw")
    void validateNotChanged_same_doesNotThrow() throws ValidationException {
        String id = "1234567890123";
        assertThatCode(() -> validator.validateNotChanged(id, id)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateNotChanged when identifier changed throws")
    void validateNotChanged_different_throws() throws ValidationException {
        assertThatThrownBy(() -> validator.validateNotChanged("1234567890123", "1234567890999"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Changing the primary identifier")
                .hasMessageContaining("CNP change request");
    }
}
