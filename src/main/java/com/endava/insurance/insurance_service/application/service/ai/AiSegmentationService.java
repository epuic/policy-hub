package com.endava.insurance.insurance_service.application.service.ai;

import com.endava.insurance.insurance_service.application.dto.ai.*;
import com.endava.insurance.insurance_service.domain.enums.AiClusterTarget;
import com.endava.insurance.insurance_service.domain.enums.PolicyStatus;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.Building;
import com.endava.insurance.insurance_service.domain.model.Client;
import com.endava.insurance.insurance_service.domain.model.Policy;
import com.endava.insurance.insurance_service.domain.model.RiskFactor;
import com.endava.insurance.insurance_service.domain.model.ai.AiClusterAssignment;
import com.endava.insurance.insurance_service.domain.model.ai.AiClusterConfiguration;
import com.endava.insurance.insurance_service.persistence.repository.BuildingRepository;
import com.endava.insurance.insurance_service.persistence.repository.ClientRepository;
import com.endava.insurance.insurance_service.persistence.repository.PolicyRepository;
import com.endava.insurance.insurance_service.persistence.repository.ai.AiClusterAssignmentRepository;
import com.endava.insurance.insurance_service.persistence.repository.ai.AiClusterConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiSegmentationService {

    private final AiDatasetBuilder datasetBuilder;
    private final AiPythonClusteringRunner clusteringRunner;
    private final AiClusterAssignmentRepository assignmentRepository;
    private final AiClusterConfigurationRepository configurationRepository;
    private final BuildingRepository buildingRepository;
    private final ClientRepository clientRepository;
    private final PolicyRepository policyRepository;

    @Value("${ai.clustering.max-iterations:50}")
    private int maxIterations;

    @Transactional
    public AiClusteringRunResponseDTO runBuildingClustering(int k) throws ValidationException {
        return runClustering(AiClusterTarget.BUILDING, k, datasetBuilder.buildBuildingRecords());
    }

    @Transactional
    public AiClusteringRunResponseDTO runClientClustering(int k) throws ValidationException {
        return runClustering(AiClusterTarget.CLIENT, k, datasetBuilder.buildClientRecords());
    }

    public List<AiClusterAssignmentResponseDTO> getAssignments(AiClusterTarget target) {
        return assignmentRepository.findByTargetOrderByClusterIdAscEntityIdAsc(target)
                .stream()
                .map(this::toAssignmentResponse)
                .toList();
    }

    public List<AiClusterConfigurationResponseDTO> getConfigurations(AiClusterTarget target) {
        return configurationRepository.findByTargetOrderByClusterIdAsc(target)
                .stream()
                .map(this::toConfigurationResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AiClusterAnalyticsDTO> getAnalytics(AiClusterTarget target) {
        return switch (target) {
            case BUILDING -> getBuildingAnalytics();
            case CLIENT -> getClientAnalytics();
        };
    }

    @Transactional
    public AiClusterConfigurationResponseDTO updateConfiguration(
            AiClusterTarget target,
            Integer clusterId,
            AiClusterConfigurationRequestDTO request
    ) throws ResourceNotFoundException {
        AiClusterConfiguration configuration = configurationRepository.findByTargetAndClusterId(target, clusterId)
                .orElseThrow(() -> new ResourceNotFoundException("AI cluster configuration not found for target "
                        + target + " and cluster " + clusterId));
        configuration.update(request.label(), request.adjustmentPercentage(), request.active());
        return toConfigurationResponse(configurationRepository.save(configuration));
    }

    private AiClusteringRunResponseDTO runClustering(AiClusterTarget target, int k, List<AiFeatureRecord> records) throws ValidationException {
        if (records.isEmpty()) {
            throw new ValidationException("No records available for AI clustering target: " + target);
        }

        AiClusteringPayload payload = new AiClusteringPayload(target, k, maxIterations, records);
        AiPythonClusteringResult result = clusteringRunner.run(payload);
        Map<Integer, AiPythonClusterSummary> summaryByCluster = result.clusters()
                .stream()
                .collect(Collectors.toMap(AiPythonClusterSummary::clusterId, Function.identity()));

        for (AiPythonAssignment assignment : result.assignments()) {
            AiPythonClusterSummary summary = summaryByCluster.get(assignment.clusterId());
            String label = summary != null ? summary.label() : "Cluster " + assignment.clusterId();
            upsertAssignment(target, assignment, label, result.algorithm());
        }

        List<AiClusterSummaryDTO> summaries = result.clusters()
                .stream()
                .map(summary -> ensureConfiguration(target, summary))
                .toList();

        log.info("AI clustering completed: target={}, records={}, clusters={}", target, records.size(), summaries.size());
        return new AiClusteringRunResponseDTO(target, result.algorithm(), records.size(), summaries.size(), summaries);
    }

    private void upsertAssignment(AiClusterTarget target, AiPythonAssignment assignment, String label, String algorithm) {
        AiClusterAssignment entity = assignmentRepository.findByTargetAndEntityId(target, assignment.entityId())
                .orElseGet(() -> new AiClusterAssignment(
                        target,
                        assignment.entityId(),
                        assignment.clusterId(),
                        label,
                        assignment.distance(),
                        algorithm
                ));
        entity.update(assignment.clusterId(), label, assignment.distance(), algorithm);
        assignmentRepository.save(entity);
    }

    private AiClusterSummaryDTO ensureConfiguration(AiClusterTarget target, AiPythonClusterSummary summary) {
        AiClusterConfiguration configuration = configurationRepository.findByTargetAndClusterId(target, summary.clusterId())
                .orElseGet(() -> configurationRepository.save(new AiClusterConfiguration(
                        target,
                        summary.clusterId(),
                        summary.label(),
                        defaultAdjustment(),
                        true
                )));

        return new AiClusterSummaryDTO(
                summary.clusterId(),
                configuration.getLabel(),
                summary.size(),
                configuration.getAdjustmentPercentage(),
                summary.numericAverages(),
                summary.categoricalModes()
        );
    }

    private BigDecimal defaultAdjustment() {
        return BigDecimal.ZERO;
    }

    private List<AiClusterAnalyticsDTO> getBuildingAnalytics() {
        Map<Long, AiClusterAssignment> assignmentsByEntity = assignmentRepository
                .findByTargetOrderByClusterIdAscEntityIdAsc(AiClusterTarget.BUILDING)
                .stream()
                .collect(Collectors.toMap(AiClusterAssignment::getEntityId, Function.identity()));
        Map<Integer, AiClusterConfiguration> configurationsByCluster = configurationRepository
                .findByTargetOrderByClusterIdAsc(AiClusterTarget.BUILDING)
                .stream()
                .collect(Collectors.toMap(AiClusterConfiguration::getClusterId, Function.identity()));
        Map<Long, List<Policy>> policiesByBuilding = policyRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(policy -> policy.getBuilding().getId()));

        Map<Integer, List<Building>> buildingsByCluster = buildingRepository.findAll()
                .stream()
                .filter(building -> assignmentsByEntity.containsKey(building.getId()))
                .collect(Collectors.groupingBy(
                        building -> assignmentsByEntity.get(building.getId()).getClusterId(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return buildingsByCluster.entrySet()
                .stream()
                .map(entry -> toBuildingAnalytics(entry.getKey(), entry.getValue(), configurationsByCluster, policiesByBuilding))
                .toList();
    }

    private List<AiClusterAnalyticsDTO> getClientAnalytics() {
        Map<Long, AiClusterAssignment> assignmentsByEntity = assignmentRepository
                .findByTargetOrderByClusterIdAscEntityIdAsc(AiClusterTarget.CLIENT)
                .stream()
                .collect(Collectors.toMap(AiClusterAssignment::getEntityId, Function.identity()));
        Map<Integer, AiClusterConfiguration> configurationsByCluster = configurationRepository
                .findByTargetOrderByClusterIdAsc(AiClusterTarget.CLIENT)
                .stream()
                .collect(Collectors.toMap(AiClusterConfiguration::getClusterId, Function.identity()));
        List<Building> buildings = buildingRepository.findAll();
        List<Policy> policies = policyRepository.findAll();
        Map<Long, List<Building>> buildingsByClient = buildings.stream()
                .collect(Collectors.groupingBy(building -> building.getOwner().getId()));
        Map<Long, List<Policy>> policiesByClient = policies.stream()
                .collect(Collectors.groupingBy(policy -> policy.getClient().getId()));

        Map<Integer, List<Client>> clientsByCluster = clientRepository.findAll()
                .stream()
                .filter(client -> assignmentsByEntity.containsKey(client.getId()))
                .collect(Collectors.groupingBy(
                        client -> assignmentsByEntity.get(client.getId()).getClusterId(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return clientsByCluster.entrySet()
                .stream()
                .map(entry -> toClientAnalytics(entry.getKey(), entry.getValue(), configurationsByCluster, buildingsByClient, policiesByClient))
                .toList();
    }

    private AiClusterAnalyticsDTO toBuildingAnalytics(Integer clusterId, List<Building> buildings,
                                                       Map<Integer, AiClusterConfiguration> configurationsByCluster,
                                                       Map<Long, List<Policy>> policiesByBuilding) {
        Map<String, BigDecimal> numeric = new LinkedHashMap<>();
        numeric.put("averageInsuredValue", average(buildings.stream().map(building -> decimal(building.getInsuredValue())).toList()));
        numeric.put("averageSurfaceArea", average(buildings.stream().map(building -> decimal(building.getSurfaceArea())).toList()));
        numeric.put("averageBuildingAge", average(buildings.stream().map(this::buildingAge).map(BigDecimal::valueOf).toList()));
        numeric.put("averageFloors", average(buildings.stream()
                .map(building -> BigDecimal.valueOf(building.getNumberOfFloors() != null ? building.getNumberOfFloors() : 0))
                .toList()));
        numeric.put("averageRiskFactorCount", average(buildings.stream().map(building -> BigDecimal.valueOf(building.getRiskFactors().size())).toList()));
        numeric.put("averagePolicyCount", average(buildings.stream()
                .map(building -> BigDecimal.valueOf(policiesByBuilding.getOrDefault(building.getId(), List.of()).size()))
                .toList()));
        numeric.put("averageFinalPremium", average(buildings.stream()
                .flatMap(building -> policiesByBuilding.getOrDefault(building.getId(), List.of()).stream())
                .map(Policy::getFinalPremium)
                .toList()));

        Map<String, String> categorical = new LinkedHashMap<>();
        categorical.put("buildingType", mode(buildings.stream()
                .map(building -> building.getType() != null ? building.getType().name() : "UNKNOWN")
                .toList()));
        categorical.put("city", mode(buildings.stream().map(building -> building.getCity().getName()).toList()));
        categorical.put("county", mode(buildings.stream().map(building -> building.getCity().getCounty().getName()).toList()));
        categorical.put("country", mode(buildings.stream().map(building -> building.getCity().getCounty().getCountry().getName()).toList()));
        categorical.put("clientType", mode(buildings.stream()
                .map(building -> building.getOwner().getType() != null ? building.getOwner().getType().name() : "UNKNOWN")
                .toList()));
        categorical.put("dominantRiskFactor", mode(buildings.stream()
                .flatMap(building -> building.getRiskFactors().stream())
                .map(RiskFactor::getType)
                .map(Enum::name)
                .toList()));

        List<AiClusterMemberDTO> members = buildings.stream()
                .limit(20)
                .map(building -> new AiClusterMemberDTO(
                        building.getId(),
                        building.getStreet() + ", Nr. " + building.getNumber(),
                        building.getOwner().getName() + " - " + building.getCity().getName(),
                        Map.of(
                                "insuredValue", decimal(building.getInsuredValue()),
                                "surfaceArea", decimal(building.getSurfaceArea()),
                                "riskFactorCount", BigDecimal.valueOf(building.getRiskFactors().size())
                        )
                ))
                .toList();

        return new AiClusterAnalyticsDTO(
                AiClusterTarget.BUILDING,
                clusterId,
                clusterLabel(clusterId, configurationsByCluster),
                buildings.size(),
                numeric,
                categorical,
                members
        );
    }

    private AiClusterAnalyticsDTO toClientAnalytics(Integer clusterId, List<Client> clients,
                                                     Map<Integer, AiClusterConfiguration> configurationsByCluster,
                                                     Map<Long, List<Building>> buildingsByClient,
                                                     Map<Long, List<Policy>> policiesByClient) {
        Map<String, BigDecimal> numeric = new LinkedHashMap<>();
        numeric.put("averageBuildingCount", average(clients.stream()
                .map(client -> BigDecimal.valueOf(buildingsByClient.getOrDefault(client.getId(), List.of()).size()))
                .toList()));
        numeric.put("averagePolicyCount", average(clients.stream()
                .map(client -> BigDecimal.valueOf(policiesByClient.getOrDefault(client.getId(), List.of()).size()))
                .toList()));
        numeric.put("averageActivePolicyCount", average(clients.stream()
                .map(client -> BigDecimal.valueOf(countPolicies(policiesByClient.getOrDefault(client.getId(), List.of()), PolicyStatus.ACTIVE)))
                .toList()));
        numeric.put("averageCancelledPolicyCount", average(clients.stream()
                .map(client -> BigDecimal.valueOf(countPolicies(policiesByClient.getOrDefault(client.getId(), List.of()), PolicyStatus.CANCELLED)))
                .toList()));
        numeric.put("averageTotalInsuredValue", average(clients.stream()
                .map(client -> buildingsByClient.getOrDefault(client.getId(), List.of())
                        .stream()
                        .map(building -> decimal(building.getInsuredValue()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .toList()));
        numeric.put("averageTotalFinalPremium", average(clients.stream()
                .map(client -> policiesByClient.getOrDefault(client.getId(), List.of())
                        .stream()
                        .map(Policy::getFinalPremium)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .toList()));

        Map<String, String> categorical = new LinkedHashMap<>();
        categorical.put("clientType", mode(clients.stream()
                .map(client -> client.getType() != null ? client.getType().name() : "UNKNOWN")
                .toList()));
        categorical.put("countryCode", mode(clients.stream().map(Client::getCountryCode).toList()));
        categorical.put("mainCity", mode(clients.stream()
                .flatMap(client -> buildingsByClient.getOrDefault(client.getId(), List.of()).stream())
                .map(building -> building.getCity().getName())
                .toList()));
        categorical.put("mainCounty", mode(clients.stream()
                .flatMap(client -> buildingsByClient.getOrDefault(client.getId(), List.of()).stream())
                .map(building -> building.getCity().getCounty().getName())
                .toList()));

        List<AiClusterMemberDTO> members = clients.stream()
                .limit(20)
                .map(client -> new AiClusterMemberDTO(
                        client.getId(),
                        client.getName(),
                        client.getIdentificationNumber(),
                        Map.of(
                                "buildingCount", BigDecimal.valueOf(buildingsByClient.getOrDefault(client.getId(), List.of()).size()),
                                "policyCount", BigDecimal.valueOf(policiesByClient.getOrDefault(client.getId(), List.of()).size()),
                                "totalInsuredValue", buildingsByClient.getOrDefault(client.getId(), List.of())
                                        .stream()
                                        .map(building -> decimal(building.getInsuredValue()))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        )
                ))
                .toList();

        return new AiClusterAnalyticsDTO(
                AiClusterTarget.CLIENT,
                clusterId,
                clusterLabel(clusterId, configurationsByCluster),
                clients.size(),
                numeric,
                categorical,
                members
        );
    }

    private String clusterLabel(Integer clusterId, Map<Integer, AiClusterConfiguration> configurationsByCluster) {
        AiClusterConfiguration configuration = configurationsByCluster.get(clusterId);
        return configuration != null ? configuration.getLabel() : "Cluster " + clusterId;
    }

    private BigDecimal average(List<BigDecimal> values) {
        List<BigDecimal> present = values.stream().filter(Objects::nonNull).toList();
        if (present.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = present.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(present.size()), 2, RoundingMode.HALF_UP);
    }

    private String mode(List<String> values) {
        return values.stream()
                .filter(value -> value != null && !value.isBlank())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    private int buildingAge(Building building) {
        if (building.getConstructionYear() == null) {
            return 0;
        }
        return Math.max(0, LocalDate.now(ZoneOffset.UTC).getYear() - building.getConstructionYear());
    }

    private long countPolicies(List<Policy> policies, PolicyStatus status) {
        return policies.stream().filter(policy -> policy.getStatus() == status).count();
    }

    private BigDecimal decimal(Double value) {
        return value != null ? BigDecimal.valueOf(value) : BigDecimal.ZERO;
    }

    private AiClusterAssignmentResponseDTO toAssignmentResponse(AiClusterAssignment assignment) {
        return new AiClusterAssignmentResponseDTO(
                assignment.getTarget(),
                assignment.getEntityId(),
                assignment.getClusterId(),
                assignment.getClusterLabel(),
                assignment.getDistance(),
                assignment.getAlgorithm(),
                assignment.getRunAt()
        );
    }

    private AiClusterConfigurationResponseDTO toConfigurationResponse(AiClusterConfiguration configuration) {
        return new AiClusterConfigurationResponseDTO(
                configuration.getId(),
                configuration.getTarget(),
                configuration.getClusterId(),
                configuration.getLabel(),
                configuration.getAdjustmentPercentage(),
                configuration.isActive()
        );
    }
}
