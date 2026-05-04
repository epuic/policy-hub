package com.endava.insurance.insurance_service.application.validator.broker.secondary;

import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.persistence.repository.BrokerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrokerExistenceValidator {

    private final BrokerRepository brokerRepository;

    public void ensureBrokerExists(Long brokerId) throws ResourceNotFoundException {
        if (!brokerRepository.existsById(brokerId)) {
            throw new ResourceNotFoundException("Broker not found with id: " + brokerId);
        }
    }
}
