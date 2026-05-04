package com.endava.insurance.insurance_service.application.validator.broker.secondary;

import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.persistence.repository.BrokerRepository;
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
@DisplayName("BrokerExistenceValidator")
@SuppressWarnings("java:S1130")
class BrokerExistenceValidatorTest {

    @Mock
    private BrokerRepository brokerRepository;

    @InjectMocks
    private BrokerExistenceValidator validator;

    @Test
    @DisplayName("ensureBrokerExists when exists – does not throw")
    void whenExists_doesNotThrow() throws ResourceNotFoundException {
        when(brokerRepository.existsById(1L)).thenReturn(true);
        assertThatCode(() -> validator.ensureBrokerExists(1L)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("ensureBrokerExists when not exists – throws ResourceNotFoundException")
    void whenNotExists_throws() {
        when(brokerRepository.existsById(999L)).thenReturn(false);
        assertThatThrownBy(() -> validator.ensureBrokerExists(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Broker not found with id: 999");
    }
}
