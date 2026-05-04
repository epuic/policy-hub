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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PolicyBusinessValidator {

    private final ClientRepository clientRepository;
    private final BuildingRepository buildingRepository;
    private final BrokerRepository brokerRepository;
    private final CurrencyRepository currencyRepository;

    public void ensureClientExists(Long clientId) throws ResourceNotFoundException {
        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client not found with id: " + clientId);
        }
    }

    public void ensureBuildingExists(Long buildingId) throws ResourceNotFoundException {
        if (!buildingRepository.existsById(buildingId)) {
            throw new ResourceNotFoundException("Building not found with id: " + buildingId);
        }
    }

    public void ensureBuildingBelongsToClient(Long buildingId, Long clientId) throws ResourceNotFoundException, ValidationException {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + buildingId));
        if (!building.getOwner().getId().equals(clientId)) {
            throw new ValidationException("Building does not belong to the specified client");
        }
    }

    public void ensureBrokerExists(Long brokerId) throws ResourceNotFoundException {
        if (!brokerRepository.existsById(brokerId)) {
            throw new ResourceNotFoundException("Broker not found with id: " + brokerId);
        }
    }

    public void ensureBrokerIsActive(Long brokerId) throws ResourceNotFoundException, ValidationException {
        Broker broker = brokerRepository.findById(brokerId)
                .orElseThrow(() -> new ResourceNotFoundException("Broker not found with id: " + brokerId));
        if (broker.getStatus() != BrokerStatus.ACTIVE) {
            throw new ValidationException("Broker is not active and cannot create policies");
        }
    }

    public void ensureCurrencyExists(Long currencyId) throws ResourceNotFoundException {
        if (!currencyRepository.existsById(currencyId)) {
            throw new ResourceNotFoundException("Currency not found with id: " + currencyId);
        }
    }

    public void ensureCurrencyIsActive(Long currencyId) throws ResourceNotFoundException, ValidationException {
        Currency currency = currencyRepository.findById(currencyId)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found with id: " + currencyId));
        if (!currency.isActive()) {
            throw new ValidationException("Currency is not active and cannot be used for policies");
        }
    }
}
