package com.endava.insurance.insurance_service.application.service.ai;

import com.endava.insurance.insurance_service.application.dto.ai.AiFeatureRecord;
import com.endava.insurance.insurance_service.domain.enums.PolicyStatus;
import com.endava.insurance.insurance_service.domain.enums.RiskFactorConfigLevel;
import com.endava.insurance.insurance_service.domain.enums.RiskFactorType;
import com.endava.insurance.insurance_service.domain.model.Building;
import com.endava.insurance.insurance_service.domain.model.Client;
import com.endava.insurance.insurance_service.domain.model.Policy;
import com.endava.insurance.insurance_service.domain.model.RiskFactor;
import com.endava.insurance.insurance_service.domain.model.geography.City;
import com.endava.insurance.insurance_service.domain.model.geography.County;
import com.endava.insurance.insurance_service.domain.model.metadata.RiskFactorConfiguration;
import com.endava.insurance.insurance_service.persistence.repository.BuildingRepository;
import com.endava.insurance.insurance_service.persistence.repository.ClientRepository;
import com.endava.insurance.insurance_service.persistence.repository.PolicyRepository;
import com.endava.insurance.insurance_service.persistence.repository.RiskFactorConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AiDatasetBuilder {

    private final BuildingRepository buildingRepository;
    private final ClientRepository clientRepository;
    private final PolicyRepository policyRepository;
    private final RiskFactorConfigurationRepository riskFactorConfigurationRepository;

    public List<AiFeatureRecord> buildBuildingRecords() {
        List<Building> buildings = buildingRepository.findAll();
        Map<Long, List<Policy>> policiesByBuilding = policyRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(policy -> policy.getBuilding().getId()));

        return buildings.stream()
                .map(building -> toBuildingRecord(building, policiesByBuilding.getOrDefault(building.getId(), List.of())))
                .toList();
    }

    public List<AiFeatureRecord> buildClientRecords() {
        List<Client> clients = clientRepository.findAll();
        List<Building> buildings = buildingRepository.findAll();
        List<Policy> policies = policyRepository.findAll();

        Map<Long, List<Building>> buildingsByClient = buildings.stream()
                .collect(Collectors.groupingBy(building -> building.getOwner().getId()));
        Map<Long, List<Policy>> policiesByClient = policies.stream()
                .collect(Collectors.groupingBy(policy -> policy.getClient().getId()));

        return clients.stream()
                .map(client -> toClientRecord(
                        client,
                        buildingsByClient.getOrDefault(client.getId(), List.of()),
                        policiesByClient.getOrDefault(client.getId(), List.of())
                ))
                .toList();
    }

    private AiFeatureRecord toBuildingRecord(Building building, List<Policy> policies) {
        Map<String, BigDecimal> numeric = new LinkedHashMap<>();
        Map<String, String> categorical = new LinkedHashMap<>();

        City city = building.getCity();
        County county = city.getCounty();

        numeric.put("buildingAge", decimal(buildingAge(building)));
        numeric.put("numberOfFloors", decimal(building.getNumberOfFloors()));
        numeric.put("surfaceArea", decimal(building.getSurfaceArea()));
        numeric.put("insuredValue", decimal(building.getInsuredValue()));
        numeric.put("riskFactorCount", decimal(building.getRiskFactors().size()));
        numeric.put("hasFloodRisk", decimal(hasRisk(building, RiskFactorType.FLOOD_ZONE)));
        numeric.put("hasEarthquakeRisk", decimal(hasRisk(building, RiskFactorType.EARTHQUAKE_RISK_ZONE)));
        numeric.put("hasWindstormRisk", decimal(hasRisk(building, RiskFactorType.WINDSTORM_ZONE)));
        numeric.put("hasLandslideRisk", decimal(hasRisk(building, RiskFactorType.LANDSLIDE_RISK)));
        numeric.put("countryRiskAdjustment", sumRiskFactorConfigs(RiskFactorConfigLevel.COUNTRY, county.getCountry().getId().toString()));
        numeric.put("countyRiskAdjustment", sumRiskFactorConfigs(RiskFactorConfigLevel.COUNTY, county.getId().toString()));
        numeric.put("cityRiskAdjustment", sumRiskFactorConfigs(RiskFactorConfigLevel.CITY, city.getId().toString()));
        numeric.put("buildingTypeRiskAdjustment", building.getType() == null
                ? BigDecimal.ZERO
                : sumRiskFactorConfigs(RiskFactorConfigLevel.BUILDING_TYPE, building.getType().name()));
        numeric.put("riskFactorTypeAdjustment", sumBuildingRiskFactorAdjustment(building));
        numeric.put("totalRiskAdjustment", calculateTotalRiskAdjustment(building));
        numeric.put("policyCount", decimal(policies.size()));
        numeric.put("activePolicyCount", decimal(countPolicies(policies, PolicyStatus.ACTIVE)));
        numeric.put("cancelledPolicyCount", decimal(countPolicies(policies, PolicyStatus.CANCELLED)));
        numeric.put("averageFinalPremium", averagePremium(policies));

        categorical.put("buildingType", building.getType() != null ? building.getType().name() : "UNKNOWN");
        categorical.put("cityId", city.getId().toString());
        categorical.put("countyId", county.getId().toString());
        categorical.put("countryId", county.getCountry().getId().toString());
        categorical.put("clientType", building.getOwner().getType().name());

        return new AiFeatureRecord(building.getId(), numeric, categorical);
    }

    private AiFeatureRecord toClientRecord(Client client, List<Building> buildings, List<Policy> policies) {
        Map<String, BigDecimal> numeric = new LinkedHashMap<>();
        Map<String, String> categorical = new LinkedHashMap<>();

        numeric.put("buildingCount", decimal(buildings.size()));
        numeric.put("policyCount", decimal(policies.size()));
        numeric.put("activePolicyCount", decimal(countPolicies(policies, PolicyStatus.ACTIVE)));
        numeric.put("draftPolicyCount", decimal(countPolicies(policies, PolicyStatus.DRAFT)));
        numeric.put("expiredPolicyCount", decimal(countPolicies(policies, PolicyStatus.EXPIRED)));
        numeric.put("cancelledPolicyCount", decimal(countPolicies(policies, PolicyStatus.CANCELLED)));
        numeric.put("totalInsuredValue", buildings.stream()
                .map(Building::getInsuredValue)
                .map(this::decimal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        numeric.put("averageInsuredValue", average(buildings.stream().map(Building::getInsuredValue).map(this::decimal).toList()));
        numeric.put("maxInsuredValue", buildings.stream().map(Building::getInsuredValue).map(this::decimal).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
        numeric.put("averageBuildingAge", average(buildings.stream().map(this::buildingAge).map(this::decimal).toList()));
        numeric.put("averageSurfaceArea", average(buildings.stream().map(Building::getSurfaceArea).map(this::decimal).toList()));
        numeric.put("totalFinalPremium", policies.stream()
                .map(Policy::getFinalPremium)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        numeric.put("averageFinalPremium", average(policies.stream().map(Policy::getFinalPremium).toList()));
        numeric.put("averageBuildingRiskAdjustment", average(buildings.stream().map(this::calculateTotalRiskAdjustment).toList()));
        numeric.put("maxBuildingRiskAdjustment", buildings.stream().map(this::calculateTotalRiskAdjustment).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
        numeric.put("riskFactorCountTotal", decimal(buildings.stream().mapToInt(building -> building.getRiskFactors().size()).sum()));

        categorical.put("clientType", client.getType().name());
        categorical.put("countryCode", client.getCountryCode());
        categorical.put("mainCityId", mode(buildings.stream().map(building -> building.getCity().getId().toString()).toList()));
        categorical.put("mainCountyId", mode(buildings.stream().map(building -> building.getCity().getCounty().getId().toString()).toList()));

        return new AiFeatureRecord(client.getId(), numeric, categorical);
    }

    private BigDecimal calculateTotalRiskAdjustment(Building building) {
        City city = building.getCity();
        County county = city.getCounty();
        BigDecimal adjustment = BigDecimal.ZERO;
        adjustment = adjustment.add(sumRiskFactorConfigs(RiskFactorConfigLevel.COUNTRY, county.getCountry().getId().toString()));
        adjustment = adjustment.add(sumRiskFactorConfigs(RiskFactorConfigLevel.COUNTY, county.getId().toString()));
        adjustment = adjustment.add(sumRiskFactorConfigs(RiskFactorConfigLevel.CITY, city.getId().toString()));
        if (building.getType() != null) {
            adjustment = adjustment.add(sumRiskFactorConfigs(RiskFactorConfigLevel.BUILDING_TYPE, building.getType().name()));
        }
        adjustment = adjustment.add(sumBuildingRiskFactorAdjustment(building));
        return adjustment;
    }

    private BigDecimal sumBuildingRiskFactorAdjustment(Building building) {
        BigDecimal adjustment = BigDecimal.ZERO;
        for (RiskFactor riskFactor : building.getRiskFactors()) {
            adjustment = adjustment.add(sumRiskFactorConfigs(RiskFactorConfigLevel.RISK_FACTOR_TYPE, riskFactor.getType().name()));
        }
        return adjustment;
    }

    private BigDecimal sumRiskFactorConfigs(RiskFactorConfigLevel level, String referenceId) {
        return riskFactorConfigurationRepository.findByActiveTrueAndLevelAndReferenceId(level, referenceId)
                .stream()
                .map(RiskFactorConfiguration::getAdjustmentPercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int buildingAge(Building building) {
        if (building.getConstructionYear() == null) {
            return 0;
        }
        int currentYear = LocalDate.now(ZoneOffset.UTC).getYear();
        return Math.max(0, currentYear - building.getConstructionYear());
    }

    private int hasRisk(Building building, RiskFactorType type) {
        return building.getRiskFactors().stream().anyMatch(riskFactor -> riskFactor.getType() == type) ? 1 : 0;
    }

    private long countPolicies(List<Policy> policies, PolicyStatus status) {
        return policies.stream().filter(policy -> policy.getStatus() == status).count();
    }

    private BigDecimal averagePremium(List<Policy> policies) {
        return average(policies.stream().map(Policy::getFinalPremium).toList());
    }

    private BigDecimal average(List<BigDecimal> values) {
        if (values == null || values.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = values.stream().filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(values.size()), 2, RoundingMode.HALF_UP);
    }

    private String mode(List<String> values) {
        return values.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(value -> value, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("UNKNOWN");
    }

    private BigDecimal decimal(Integer value) {
        return value == null ? BigDecimal.ZERO : BigDecimal.valueOf(value);
    }

    private BigDecimal decimal(long value) {
        return BigDecimal.valueOf(value);
    }

    private BigDecimal decimal(Double value) {
        return value == null ? BigDecimal.ZERO : BigDecimal.valueOf(value);
    }

    private BigDecimal decimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
