package com.endava.insurance.insurance_service.application.validator.broker;

import com.endava.insurance.insurance_service.application.dto.broker.BrokerCreateDTO;
import com.endava.insurance.insurance_service.application.dto.broker.BrokerUpdateDTO;
import com.endava.insurance.insurance_service.application.validator.broker.secondary.BrokerExistenceValidator;
import com.endava.insurance.insurance_service.application.validator.broker.secondary.BrokerUniquenessValidator;
import com.endava.insurance.insurance_service.domain.enums.BrokerStatus;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.Broker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BrokerValidator")
@SuppressWarnings("java:S1130")
class BrokerValidatorTest {

    @Mock
    private BrokerExistenceValidator brokerExistenceValidator;

    @Mock
    private BrokerUniquenessValidator brokerUniquenessValidator;

    @InjectMocks
    private BrokerValidator validator;

    @Test
    @DisplayName("validateNewBroker delegates to uniqueness and does not throw")
    void validateNewBroker_delegates_doesNotThrow() throws ValidationException {
        BrokerCreateDTO request = new BrokerCreateDTO("BRK1", "Broker One", "b@b.com", "+40111", "password123", BrokerStatus.ACTIVE, BigDecimal.ONE);
        doNothing().when(brokerUniquenessValidator).ensureEmailUnique("b@b.com");
        doNothing().when(brokerUniquenessValidator).ensurePhoneUnique("+40111");

        assertThatCode(() -> validator.validateNewBroker(request)).doesNotThrowAnyException();
        verify(brokerUniquenessValidator).ensureEmailUnique("b@b.com");
        verify(brokerUniquenessValidator).ensurePhoneUnique("+40111");
    }

    @Test
    @DisplayName("validateBrokerUpdate when email changed and unique – does not throw")
    void validateBrokerUpdate_emailChanged_doesNotThrow() throws ValidationException {
        Broker existing = mock(Broker.class);
        when(existing.getEmail()).thenReturn("old@b.com");
        when(existing.getPhone()).thenReturn("+40111");
        BrokerUpdateDTO request = new BrokerUpdateDTO("Name", "new@b.com", "+40111", BigDecimal.ONE);
        doNothing().when(brokerUniquenessValidator).ensureEmailUniqueIfChanged("old@b.com", "new@b.com");
        doNothing().when(brokerUniquenessValidator).ensurePhoneUniqueIfChanged("+40111", "+40111");

        assertThatCode(() -> validator.validateBrokerUpdate(existing, request)).doesNotThrowAnyException();
    }
}
