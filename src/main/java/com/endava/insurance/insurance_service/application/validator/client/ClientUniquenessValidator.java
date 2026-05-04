package com.endava.insurance.insurance_service.application.validator.client;

import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.persistence.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientUniquenessValidator {

    private final ClientRepository clientRepository;

    public void ensureEmailUnique(String email) throws ValidationException {
        if (clientRepository.existsByEmail(email)) {
            throw new ValidationException("Email address is already in use: " + email);
        }
    }

    public void ensureEmailUniqueIfChanged(String existingEmail, String newEmail) throws ValidationException {
        if (!existingEmail.equals(newEmail) && clientRepository.existsByEmail(newEmail)) {
            throw new ValidationException("New email address is already in use by another client.");
        }
    }

    public void ensurePhoneUnique(String phone) throws ValidationException {
        if (clientRepository.existsByPhone(phone)) {
            throw new ValidationException("Phone number is already in use: " + phone);
        }
    }

    public void ensurePhoneUniqueIfChanged(String existingPhone, String newPhone) throws ValidationException {
        if (!existingPhone.equals(newPhone) && clientRepository.existsByPhone(newPhone)) {
            throw new ValidationException("New phone number is already in use by another client.");
        }
    }
}
