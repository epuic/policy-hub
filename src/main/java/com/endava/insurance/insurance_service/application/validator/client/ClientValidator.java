package com.endava.insurance.insurance_service.application.validator.client;

import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.Client;
import com.endava.insurance.insurance_service.application.dto.client.ClientCreateDTO;
import com.endava.insurance.insurance_service.application.dto.client.ClientUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientValidator {

    private final IdentificationNumberValidator identificationNumberValidator;
    private final ClientUniquenessValidator clientUniquenessValidator;

    public void validateNewClient(ClientCreateDTO request) throws ValidationException {
        identificationNumberValidator.validateFormat(
                request.countryCode(),
                request.type(),
                request.identificationNumber()
        );
        identificationNumberValidator.validateUnique(request.identificationNumber());
        clientUniquenessValidator.ensureEmailUnique(request.email());
        clientUniquenessValidator.ensurePhoneUnique(request.phone());
    }


    public void validateClientUpdate(Client existingClient, ClientUpdateDTO request) throws ValidationException {
        clientUniquenessValidator.ensureEmailUniqueIfChanged(existingClient.getEmail(), request.email());
        clientUniquenessValidator.ensurePhoneUniqueIfChanged(existingClient.getPhone(), request.phone());
    }
}