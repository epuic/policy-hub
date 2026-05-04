package com.endava.insurance.insurance_service.application.validator.policy.secondary;

import com.endava.insurance.insurance_service.domain.enums.BrokerStatus;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.Building;
import com.endava.insurance.insurance_service.domain.model.Broker;
import com.endava.insurance.insurance_service.domain.model.metadata.Currency;
import com.endava.insurance.insurance_service.persistence.repository.BuildingRepository;
import com.endava.insurance.insurance_service.persistence.repository.ClientRepository;
import com.endava.insurance.insurance_service.persistence.repository.CurrencyRepository;
import com.endava.insurance.insurance_service.persistence.repository.BrokerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PolicyBusinessValidator")
@SuppressWarnings("java:S1130")
class PolicyBusinessValidatorTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private BuildingRepository buildingRepository;
    @Mock
    private BrokerRepository brokerRepository;
    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private PolicyBusinessValidator validator;

    @Nested
    @DisplayName("ensureClientExists")
    class EnsureClientExists {

        @Test
        @DisplayName("when exists – does not throw")
        void whenExists_doesNotThrow() throws ResourceNotFoundException {
            when(clientRepository.existsById(1L)).thenReturn(true);
            assertThatCode(() -> validator.ensureClientExists(1L)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("when not exists – throws")
        void whenNotExists_throws() {
            when(clientRepository.existsById(999L)).thenReturn(false);
            assertThatThrownBy(() -> validator.ensureClientExists(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Client not found");
        }
    }

    @Nested
    @DisplayName("ensureBuildingExists")
    class EnsureBuildingExists {

        @Test
        @DisplayName("when exists – does not throw")
        void whenExists_doesNotThrow() throws ResourceNotFoundException {
            when(buildingRepository.existsById(1L)).thenReturn(true);
            assertThatCode(() -> validator.ensureBuildingExists(1L)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("when not exists – throws")
        void whenNotExists_throws() {
            when(buildingRepository.existsById(999L)).thenReturn(false);
            assertThatThrownBy(() -> validator.ensureBuildingExists(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Building not found");
        }
    }

    @Nested
    @DisplayName("ensureBuildingBelongsToClient")
    class EnsureBuildingBelongsToClient {

        @Test
        @DisplayName("when building not found – throws ResourceNotFoundException")
        void whenBuildingNotFound_throws() {
            when(buildingRepository.findById(999L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> validator.ensureBuildingBelongsToClient(999L, 1L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Building not found with id: 999");
        }

        @Test
        @DisplayName("when building belongs to client – does not throw")
        void whenBelongs_doesNotThrow() throws ResourceNotFoundException, ValidationException {
            Building building = mock(Building.class);
            var owner = mock(com.endava.insurance.insurance_service.domain.model.Client.class);
            when(building.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(1L);
            when(buildingRepository.findById(2L)).thenReturn(Optional.of(building));
            assertThatCode(() -> validator.ensureBuildingBelongsToClient(2L, 1L)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("when building does not belong to client – throws ValidationException")
        void whenNotBelongs_throws() {
            Building building = mock(Building.class);
            var owner = mock(com.endava.insurance.insurance_service.domain.model.Client.class);
            when(building.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(99L);
            when(buildingRepository.findById(2L)).thenReturn(Optional.of(building));
            assertThatThrownBy(() -> validator.ensureBuildingBelongsToClient(2L, 1L))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Building does not belong");
        }
    }

    @Nested
    @DisplayName("ensureBrokerExists")
    class EnsureBrokerExists {

        @Test
        @DisplayName("when exists – does not throw")
        void whenExists_doesNotThrow() throws ResourceNotFoundException {
            when(brokerRepository.existsById(1L)).thenReturn(true);
            assertThatCode(() -> validator.ensureBrokerExists(1L)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("when not exists – throws")
        void whenNotExists_throws() {
            when(brokerRepository.existsById(999L)).thenReturn(false);
            assertThatThrownBy(() -> validator.ensureBrokerExists(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Broker not found");
        }
    }

    @Nested
    @DisplayName("ensureBrokerIsActive")
    class EnsureBrokerIsActive {

        @Test
        @DisplayName("when broker not found – throws ResourceNotFoundException")
        void whenBrokerNotFound_throws() {
            when(brokerRepository.findById(999L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> validator.ensureBrokerIsActive(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Broker not found with id: 999");
        }

        @Test
        @DisplayName("when broker is ACTIVE – does not throw")
        void whenActive_doesNotThrow() throws ResourceNotFoundException, ValidationException {
            Broker broker = mock(Broker.class);
            when(broker.getStatus()).thenReturn(BrokerStatus.ACTIVE);
            when(brokerRepository.findById(1L)).thenReturn(Optional.of(broker));
            assertThatCode(() -> validator.ensureBrokerIsActive(1L)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("when broker is not ACTIVE – throws ValidationException")
        void whenInactive_throws() {
            Broker broker = mock(Broker.class);
            when(broker.getStatus()).thenReturn(BrokerStatus.INACTIVE);
            when(brokerRepository.findById(1L)).thenReturn(Optional.of(broker));
            assertThatThrownBy(() -> validator.ensureBrokerIsActive(1L))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Broker is not active");
        }
    }

    @Nested
    @DisplayName("ensureCurrencyExists")
    class EnsureCurrencyExists {

        @Test
        @DisplayName("when exists – does not throw")
        void whenExists_doesNotThrow() throws ResourceNotFoundException {
            when(currencyRepository.existsById(1L)).thenReturn(true);
            assertThatCode(() -> validator.ensureCurrencyExists(1L)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("when not exists – throws")
        void whenNotExists_throws() {
            when(currencyRepository.existsById(999L)).thenReturn(false);
            assertThatThrownBy(() -> validator.ensureCurrencyExists(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Currency not found");
        }
    }

    @Nested
    @DisplayName("ensureCurrencyIsActive")
    class EnsureCurrencyIsActive {

        @Test
        @DisplayName("when currency not found – throws ResourceNotFoundException")
        void whenCurrencyNotFound_throws() {
            when(currencyRepository.findById(999L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> validator.ensureCurrencyIsActive(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Currency not found with id: 999");
        }

        @Test
        @DisplayName("when currency is active – does not throw")
        void whenActive_doesNotThrow() throws ResourceNotFoundException, ValidationException {
            Currency currency = mock(Currency.class);
            when(currency.isActive()).thenReturn(true);
            when(currencyRepository.findById(1L)).thenReturn(Optional.of(currency));
            assertThatCode(() -> validator.ensureCurrencyIsActive(1L)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("when currency is inactive – throws ValidationException")
        void whenInactive_throws() {
            Currency currency = mock(Currency.class);
            when(currency.isActive()).thenReturn(false);
            when(currencyRepository.findById(1L)).thenReturn(Optional.of(currency));
            assertThatThrownBy(() -> validator.ensureCurrencyIsActive(1L))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Currency is not active");
        }
    }
}
