package com.endava.insurance.insurance_service.application.validator.metadata.secondary;

import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.persistence.repository.RiskFactorConfigurationRepository;
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
@DisplayName("RiskFactorConfigurationExistenceValidator")
@SuppressWarnings("java:S1130")
class RiskFactorConfigurationExistenceValidatorTest {

    @Mock
    private RiskFactorConfigurationRepository riskFactorConfigurationRepository;

    @InjectMocks
    private RiskFactorConfigurationExistenceValidator validator;

    @Test
    @DisplayName("ensureRiskFactorConfigurationExists when exists – does not throw")
    void whenExists_doesNotThrow() throws ResourceNotFoundException {
        when(riskFactorConfigurationRepository.existsById(1L)).thenReturn(true);
        assertThatCode(() -> validator.ensureRiskFactorConfigurationExists(1L)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("ensureRiskFactorConfigurationExists when not exists – throws")
    void whenNotExists_throws() {
        when(riskFactorConfigurationRepository.existsById(999L)).thenReturn(false);
        assertThatThrownBy(() -> validator.ensureRiskFactorConfigurationExists(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Risk factor configuration not found with id: 999");
    }
}
