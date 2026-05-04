package com.endava.insurance.insurance_service.application.validator.client;

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
@DisplayName("ClientUniquenessValidator")
class ClientUniquenessValidatorTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientUniquenessValidator validator;

    @Test
    @DisplayName("ensureEmailUnique when email exists throws")
    void ensureEmailUnique_exists_throws() {
        String email = "used@example.com";
        when(clientRepository.existsByEmail(email)).thenReturn(true);

        assertThatThrownBy(() -> validator.ensureEmailUnique(email))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Email address is already in use: " + email);
    }

    @Test
    @DisplayName("ensureEmailUnique when email does not exist does not throw")
    void ensureEmailUnique_notExists_doesNotThrow() {
        when(clientRepository.existsByEmail("free@example.com")).thenReturn(false);

        assertThatCode(() -> validator.ensureEmailUnique("free@example.com")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("ensureEmailUniqueIfChanged when same email does not throw")
    void ensureEmailUniqueIfChanged_sameEmail_doesNotThrow() {
        assertThatCode(() -> validator.ensureEmailUniqueIfChanged("a@b.com", "a@b.com")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("ensureEmailUniqueIfChanged when different and new exists throws")
    void ensureEmailUniqueIfChanged_differentAndNewExists_throws() {
        when(clientRepository.existsByEmail("new@example.com")).thenReturn(true);

        assertThatThrownBy(() -> validator.ensureEmailUniqueIfChanged("old@example.com", "new@example.com"))
                .isInstanceOf(ValidationException.class)
                .hasMessage("New email address is already in use by another client.");
    }

    @Test
    @DisplayName("ensureEmailUniqueIfChanged when different and new free does not throw")
    void ensureEmailUniqueIfChanged_differentAndNewFree_doesNotThrow() {
        when(clientRepository.existsByEmail("new@example.com")).thenReturn(false);

        assertThatCode(() -> validator.ensureEmailUniqueIfChanged("old@example.com", "new@example.com"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("ensurePhoneUnique when phone exists throws")
    void ensurePhoneUnique_exists_throws() {
        String phone = "+40711111111";
        when(clientRepository.existsByPhone(phone)).thenReturn(true);

        assertThatThrownBy(() -> validator.ensurePhoneUnique(phone))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Phone number is already in use: " + phone);
    }

    @Test
    @DisplayName("ensurePhoneUnique when phone does not exist does not throw")
    void ensurePhoneUnique_notExists_doesNotThrow() {
        when(clientRepository.existsByPhone("+40799999999")).thenReturn(false);

        assertThatCode(() -> validator.ensurePhoneUnique("+40799999999")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("ensurePhoneUniqueIfChanged when same phone does not throw")
    void ensurePhoneUniqueIfChanged_samePhone_doesNotThrow() {
        assertThatCode(() -> validator.ensurePhoneUniqueIfChanged("+40711111111", "+40711111111"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("ensurePhoneUniqueIfChanged when different and new exists throws")
    void ensurePhoneUniqueIfChanged_differentAndNewExists_throws() {
        when(clientRepository.existsByPhone("+40722222222")).thenReturn(true);

        assertThatThrownBy(() -> validator.ensurePhoneUniqueIfChanged("+40711111111", "+40722222222"))
                .isInstanceOf(ValidationException.class)
                .hasMessage("New phone number is already in use by another client.");
    }

    @Test
    @DisplayName("ensurePhoneUniqueIfChanged when different and new free does not throw")
    void ensurePhoneUniqueIfChanged_differentAndNewFree_doesNotThrow() {
        when(clientRepository.existsByPhone("+40722222222")).thenReturn(false);

        assertThatCode(() -> validator.ensurePhoneUniqueIfChanged("+40711111111", "+40722222222"))
                .doesNotThrowAnyException();
    }
}
