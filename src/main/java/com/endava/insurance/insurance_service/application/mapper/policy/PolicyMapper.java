package com.endava.insurance.insurance_service.application.mapper.policy;

import com.endava.insurance.insurance_service.application.dto.policy.PolicyCreateDTO;
import com.endava.insurance.insurance_service.application.dto.policy.PolicyResponseDTO;
import com.endava.insurance.insurance_service.application.dto.policy.PolicySummaryDTO;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.Broker;
import com.endava.insurance.insurance_service.domain.model.Building;
import com.endava.insurance.insurance_service.domain.model.Client;
import com.endava.insurance.insurance_service.domain.model.Policy;
import com.endava.insurance.insurance_service.domain.model.metadata.Currency;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PolicyMapper {

    private final EntityManager entityManager;

    public Policy toEntity(PolicyCreateDTO request, String policyNumber, BigDecimal finalPremium) throws ValidationException {
        Client client = entityManager.getReference(Client.class, request.clientId());
        Building building = entityManager.getReference(Building.class, request.buildingId());
        Broker broker = entityManager.getReference(Broker.class, request.brokerId());
        Currency currency = entityManager.getReference(Currency.class, request.currencyId());

        return new Policy(
                policyNumber,
                new Policy.PolicyParties(client, building, broker),
                request.startDate(),
                request.endDate(),
                request.basePremiumAmount(),
                currency,
                finalPremium
        );
    }

    public PolicyResponseDTO toResponse(Policy policy) {
        var client = policy.getClient();
        var building = policy.getBuilding();
        var broker = policy.getBroker();
        var currency = policy.getCurrency();
        
        var city = building.getCity();
        var county = city.getCounty();
        var country = county.getCountry();

        String buildingAddress = String.format("%s, Nr. %s, %s", 
                building.getStreet(), 
                building.getNumber(), 
                city.getName());

        return new PolicyResponseDTO(
                policy.getId(),
                policy.getPolicyNumber(),
                client.getId(),
                client.getName(),
                building.getId(),
                buildingAddress,
                city.getName(),
                county.getName(),
                country.getName(),
                broker.getId(),
                broker.getName(),
                policy.getStatus(),
                policy.getStartDate(),
                policy.getEndDate(),
                policy.getBasePremiumAmount(),
                currency.getCode(),
                policy.getFinalPremium(),
                policy.getCreatedAt(),
                policy.getLastUpdatedAt(),
                policy.getCancellationDate(),
                policy.getCancellationReason()
        );
    }

    public PolicySummaryDTO toSummary(Policy policy) {
        var currency = policy.getCurrency();
        return new PolicySummaryDTO(
                policy.getId(),
                policy.getPolicyNumber(),
                policy.getStatus(),
                policy.getStartDate(),
                policy.getEndDate(),
                policy.getFinalPremium(),
                currency != null ? currency.getCode() : null,
                policy.getCreatedAt()
        );
    }
}
