package com.endava.insurance.insurance_service.application.validator.broker.secondary;

import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.persistence.repository.BrokerAuthRepository;
import com.endava.insurance.insurance_service.persistence.repository.BrokerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrokerUniquenessValidator {

    private final BrokerRepository brokerRepository;
    private final BrokerAuthRepository brokerAuthRepository;

    public void ensureEmailUnique(String email) throws ValidationException {
        if (email != null && !email.trim().isEmpty()) {
            String emailTrimmed = email.trim();
            if (brokerRepository.existsByEmailIgnoreCase(emailTrimmed)
                    || brokerAuthRepository.existsByEmailIgnoreCase(emailTrimmed)) {
                throw new ValidationException("Email address is already in use: " + emailTrimmed);
            }
        }
    }

    public void ensurePhoneUnique(String phone) throws ValidationException {
        if (phone != null && !phone.trim().isEmpty() && brokerRepository.existsByPhone(phone)) {
            throw new ValidationException("Phone number is already in use: " + phone);
        }
    }

    public void ensureEmailUniqueIfChanged(String existingEmail, String newEmail) throws ValidationException {
        if (newEmail != null && !newEmail.trim().isEmpty()) {
            String existingEmailTrimmed = existingEmail != null ? existingEmail.trim() : "";
            String newEmailTrimmed = newEmail.trim();
            if (!existingEmailTrimmed.equalsIgnoreCase(newEmailTrimmed)
                    && (brokerRepository.existsByEmailIgnoreCase(newEmailTrimmed)
                    || brokerAuthRepository.existsByEmailIgnoreCase(newEmailTrimmed))) {
                throw new ValidationException("New email address is already in use by another broker.");
            }
        }
    }

    public void ensurePhoneUniqueIfChanged(String existingPhone, String newPhone) throws ValidationException {
        if (newPhone != null && !newPhone.trim().isEmpty()) {
            String existingPhoneTrimmed = existingPhone != null ? existingPhone.trim() : "";
            String newPhoneTrimmed = newPhone.trim();
            if (!existingPhoneTrimmed.equals(newPhoneTrimmed) && brokerRepository.existsByPhone(newPhoneTrimmed)) {
                throw new ValidationException("New phone number is already in use by another broker.");
            }
        }
    }

}
