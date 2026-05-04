package com.endava.insurance.insurance_service.application.validator.broker;

import com.endava.insurance.insurance_service.application.dto.broker.BrokerCreateDTO;
import com.endava.insurance.insurance_service.application.dto.broker.BrokerUpdateDTO;
import com.endava.insurance.insurance_service.application.validator.broker.secondary.BrokerExistenceValidator;
import com.endava.insurance.insurance_service.application.validator.broker.secondary.BrokerUniquenessValidator;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.Broker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrokerValidator {

    private final BrokerExistenceValidator brokerExistenceValidator;
    private final BrokerUniquenessValidator brokerUniquenessValidator;

    public void validateNewBroker(BrokerCreateDTO request) throws ValidationException {
        brokerUniquenessValidator.ensureEmailUnique(request.email());
        brokerUniquenessValidator.ensurePhoneUnique(request.phone());
    }

    public void validateBrokerUpdate(Broker existingBroker, BrokerUpdateDTO request) throws ValidationException {
        brokerUniquenessValidator.ensureEmailUniqueIfChanged(existingBroker.getEmail(), request.email());
        brokerUniquenessValidator.ensurePhoneUniqueIfChanged(existingBroker.getPhone(), request.phone());
    }
}
