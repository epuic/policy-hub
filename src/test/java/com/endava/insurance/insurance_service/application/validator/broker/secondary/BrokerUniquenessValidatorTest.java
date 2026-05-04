package com.endava.insurance.insurance_service.application.validator.broker.secondary;

import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.persistence.repository.BrokerAuthRepository;
import com.endava.insurance.insurance_service.persistence.repository.BrokerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BrokerUniquenessValidator")
@SuppressWarnings("java:S1130")
class BrokerUniquenessValidatorTest {

    @Mock
    private BrokerRepository brokerRepository;

    @Mock
    private BrokerAuthRepository brokerAuthRepository;

    @InjectMocks
    private BrokerUniquenessValidator validator;

    @Nested
    @DisplayName("ensureEmailUnique")
    class EnsureEmailUnique {

        @Test
        @DisplayName("when email null – does not throw")
        void emailNull_doesNotThrow() throws ValidationException {
            assertThatCode(() -> validator.ensureEmailUnique(null)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("when email blank – does not throw")
        void emailBlank_doesNotThrow() throws ValidationException {
            assertThatCode(() -> validator.ensureEmailUnique("   ")).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("when email not in use – does not throw")
        void emailNotInUse_doesNotThrow() throws ValidationException {
            when(brokerAuthRepository.existsByEmail("new@b.com")).thenReturn(false);
            assertThatCode(() -> validator.ensureEmailUnique("new@b.com")).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("when email already in use – throws ValidationException")
        void emailInUse_throws() {
            when(brokerAuthRepository.existsByEmail("used@b.com")).thenReturn(true);
            assertThatThrownBy(() -> validator.ensureEmailUnique("used@b.com"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Email address is already in use")
                    .hasMessageContaining("used@b.com");
        }
    }

    @Nested
    @DisplayName("ensurePhoneUnique")
    class EnsurePhoneUnique {

        @Test
        @DisplayName("when phone null – does not throw")
        void phoneNull_doesNotThrow() throws ValidationException {
            assertThatCode(() -> validator.ensurePhoneUnique(null)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("when phone not in use – does not throw")
        void phoneNotInUse_doesNotThrow() throws ValidationException {
            when(brokerRepository.existsByPhone("+40123456789")).thenReturn(false);
            assertThatCode(() -> validator.ensurePhoneUnique("+40123456789")).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("when phone already in use – throws ValidationException")
        void phoneInUse_throws() {
            when(brokerRepository.existsByPhone("+40999888777")).thenReturn(true);
            assertThatThrownBy(() -> validator.ensurePhoneUnique("+40999888777"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Phone number is already in use");
        }
    }

    @Nested
    @DisplayName("ensureEmailUniqueIfChanged")
    class EnsureEmailUniqueIfChanged {

        @Test
        @DisplayName("when new email null – does not throw")
        void newEmailNull_doesNotThrow() throws ValidationException {
            assertThatCode(() -> validator.ensureEmailUniqueIfChanged("old@b.com", null)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("when email unchanged – does not throw")
        void emailUnchanged_doesNotThrow() throws ValidationException {
            assertThatCode(() -> validator.ensureEmailUniqueIfChanged("same@b.com", "same@b.com")).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("when email changed and unique – does not throw")
        void emailChangedAndUnique_doesNotThrow() throws ValidationException {
            when(brokerAuthRepository.existsByEmail("new@b.com")).thenReturn(false);
            assertThatCode(() -> validator.ensureEmailUniqueIfChanged("old@b.com", "new@b.com")).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("when email changed and already in use – throws")
        void emailChangedAndInUse_throws() {
            when(brokerAuthRepository.existsByEmail("taken@b.com")).thenReturn(true);
            assertThatThrownBy(() -> validator.ensureEmailUniqueIfChanged("old@b.com", "taken@b.com"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("New email address is already in use");
        }
    }

    @Nested
    @DisplayName("ensurePhoneUniqueIfChanged")
    class EnsurePhoneUniqueIfChanged {

        @Test
        @DisplayName("when new phone empty – does not throw")
        void newPhoneEmpty_doesNotThrow() throws ValidationException {
            assertThatCode(() -> validator.ensurePhoneUniqueIfChanged("+40111", "")).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("when phone unchanged – does not throw")
        void phoneUnchanged_doesNotThrow() throws ValidationException {
            assertThatCode(() -> validator.ensurePhoneUniqueIfChanged("+40111", "+40111")).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("when phone changed and unique – does not throw")
        void phoneChangedAndUnique_doesNotThrow() throws ValidationException {
            when(brokerRepository.existsByPhone("+40222")).thenReturn(false);
            assertThatCode(() -> validator.ensurePhoneUniqueIfChanged("+40111", "+40222")).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("when phone changed and already in use – throws")
        void phoneChangedAndInUse_throws() {
            when(brokerRepository.existsByPhone("+40999")).thenReturn(true);
            assertThatThrownBy(() -> validator.ensurePhoneUniqueIfChanged("+40111", "+40999"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("New phone number is already in use");
        }
    }
}
