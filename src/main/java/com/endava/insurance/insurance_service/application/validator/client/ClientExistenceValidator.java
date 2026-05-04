package com.endava.insurance.insurance_service.application.validator.client;

import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.persistence.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientExistenceValidator {

    private final ClientRepository clientRepository;

    public void ensureClientExists(Long clientId) throws ResourceNotFoundException {
        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client not found with id: " + clientId);
        }
    }
}
