package com.endava.insurance.insurance_service.application.validator.client;

import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
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
@DisplayName("ClientExistenceValidator")
@SuppressWarnings("java:S1130")
class ClientExistenceValidatorTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientExistenceValidator validator;

    @Test
    @DisplayName("ensureClientExists when exists – does not throw")
    void whenExists_doesNotThrow() throws ResourceNotFoundException {
        when(clientRepository.existsById(1L)).thenReturn(true);
        assertThatCode(() -> validator.ensureClientExists(1L)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("ensureClientExists when not exists – throws ResourceNotFoundException")
    void whenNotExists_throws() {
        when(clientRepository.existsById(999L)).thenReturn(false);
        assertThatThrownBy(() -> validator.ensureClientExists(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Client not found with id: 999");
    }
}
