package com.endava.insurance.insurance_service.application.validator.client;

import com.endava.insurance.insurance_service.domain.enums.ClientType;
import com.endava.insurance.insurance_service.application.dto.client.ClientCreateDTO;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientValidator - unit tests")
class ClientValidatorTest {

    @Mock
    private IdentificationNumberValidator identificationNumberValidator;

    @Mock
    private ClientUniquenessValidator clientUniquenessValidator;

    @InjectMocks
    private ClientValidator validator;

    @Test
    @DisplayName("duplicate identification number should fail")
    void validateNewClient_duplicateIdentificationNumber_throws() throws ValidationException {
        var request = new ClientCreateDTO(
                "RO",
                ClientType.INDIVIDUAL,
                "Test Client",
                "1234567890123",
                "test@example.com",
                "0712345678",
                null
        );
        doThrow(new ValidationException("Identification number already exists: 1234567890123"))
                .when(identificationNumberValidator).validateUnique("1234567890123");

        assertThatThrownBy(() -> validator.validateNewClient(request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Identification number already exists");
        verify(identificationNumberValidator).validateFormat("RO", ClientType.INDIVIDUAL, "1234567890123");
        verify(identificationNumberValidator).validateUnique("1234567890123");
        verifyNoInteractions(clientUniquenessValidator);
    }
}
