package com.endava.insurance.insurance_service.application.mapper.building;

import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.enums.RiskFactorType;
import com.endava.insurance.insurance_service.domain.model.Building;
import com.endava.insurance.insurance_service.domain.model.Client;
import com.endava.insurance.insurance_service.domain.model.geography.City;
import com.endava.insurance.insurance_service.application.dto.building.BuildingRequestDTO;
import com.endava.insurance.insurance_service.application.dto.building.BuildingResponseDTO;
import com.endava.insurance.insurance_service.application.dto.building.BuildingResponseDTOV2;
import com.endava.insurance.insurance_service.application.dto.policy.PolicySummaryDTO;
import com.endava.insurance.insurance_service.application.mapper.policy.PolicyMapper;
import com.endava.insurance.insurance_service.domain.model.Policy;
import com.endava.insurance.insurance_service.persistence.repository.RiskFactorRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BuildingMapper {

    private final EntityManager entityManager;
    private final RiskFactorRepository riskFactorRepository;
    private final PolicyMapper policyMapper;

    public Building toEntity(BuildingRequestDTO request, Long clientId) throws ValidationException {
        Client owner = entityManager.getReference(Client.class, clientId);
        City city = entityManager.getReference(City.class, request.cityId());

        var attrs = new Building.BuildingAttributes(
                request.constructionYear(),
                request.type(),
                request.numberOfFloors(),
                request.surfaceArea(),
                request.insuredValue()
        );
        Building building = new Building(owner, city, request.street(), request.number(), attrs);
        List<RiskFactorType> types = request.riskFactorTypes() != null ? request.riskFactorTypes() : Collections.emptyList();
        building.replaceRiskFactors(riskFactorRepository.findByTypeIn(types));
        return building;
    }

    public void updateEntityFromRequest(BuildingRequestDTO request, Building building) throws ValidationException {
        City city = entityManager.getReference(City.class, request.cityId());
        var attrs = new Building.BuildingAttributes(
                request.constructionYear(),
                request.type(),
                request.numberOfFloors(),
                request.surfaceArea(),
                request.insuredValue()
        );
        building.updateDetails(request.street(), request.number(), city, attrs);
        List<RiskFactorType> types = request.riskFactorTypes() != null ? request.riskFactorTypes() : Collections.emptyList();
        building.replaceRiskFactors(riskFactorRepository.findByTypeIn(types));
    }

    public BuildingResponseDTO toResponse(Building building) {
        var city = building.getCity();
        var county = city.getCounty();
        var country = county.getCountry();

        List<RiskFactorType> riskFactorTypes = building.getRiskFactors().stream()
                .map(rf -> rf.getType())
                .sorted()
                .toList();

        return new BuildingResponseDTO(
                building.getId(),
                building.getOwner().getId(),
                building.getOwner().getName(),
                String.format("%s, Nr. %s, %s", building.getStreet(), building.getNumber(), building.getCity().getName()),
                city.getName(),
                county.getName(),
                country.getName(),
                building.getConstructionYear(),
                building.getType(),
                building.getNumberOfFloors(),
                building.getSurfaceArea(),
                building.getInsuredValue(),
                riskFactorTypes
        );
    }

    public BuildingResponseDTOV2 toResponseV2(Building building, List<Policy> policies) {
        BuildingResponseDTO base = toResponse(building);
        List<PolicySummaryDTO> policySummaries = policies != null
                ? policies.stream().map(policyMapper::toSummary).toList()
                : List.of();
        return new BuildingResponseDTOV2(
                base.id(),
                base.clientId(),
                base.clientName(),
                base.fullAddress(),
                base.cityName(),
                base.countyName(),
                base.countryName(),
                base.constructionYear(),
                base.type(),
                base.numberOfFloors(),
                base.surfaceArea(),
                base.insuredValue(),
                base.riskFactorTypes(),
                policySummaries
        );
    }
}