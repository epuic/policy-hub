package com.endava.insurance.insurance_service.application.mapper.broker;

import com.endava.insurance.insurance_service.application.dto.broker.BrokerCreateDTO;
import com.endava.insurance.insurance_service.application.dto.broker.BrokerResponseDTO;
import com.endava.insurance.insurance_service.application.dto.broker.BrokerUpdateDTO;
import com.endava.insurance.insurance_service.domain.enums.BrokerStatus;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.Broker;
import org.springframework.stereotype.Component;

@Component
public class BrokerMapper {

    public Broker toEntity(BrokerCreateDTO request) throws ValidationException {
        return new Broker(
                request.brokerCode(),
                request.name(),
                request.email(),
                request.phone(),
                request.status(),
                request.commissionPercentage()
        );
    }

    public BrokerResponseDTO toResponse(Broker broker) {
        return new BrokerResponseDTO(
                broker.getId(),
                broker.getBrokerCode(),
                broker.getName(),
                broker.getEmail(),
                broker.getPhone(),
                broker.getStatus(),
                broker.getCommissionPercentage()
        );
    }

    public void updateEntityFromRequest(BrokerUpdateDTO request, Broker broker) throws ValidationException {
        broker.updateDetails(
                request.name(),
                request.email(),
                request.phone(),
                request.commissionPercentage()
        );
    }
}
