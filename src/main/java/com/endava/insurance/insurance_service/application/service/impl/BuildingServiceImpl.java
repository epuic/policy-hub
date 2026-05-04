package com.endava.insurance.insurance_service.application.service.impl;

import com.endava.insurance.insurance_service.domain.model.Building;
import com.endava.insurance.insurance_service.application.dto.building.BuildingRequestDTO;
import com.endava.insurance.insurance_service.application.dto.building.BuildingResponseDTO;
import com.endava.insurance.insurance_service.application.dto.building.BuildingResponseDTOV2;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.application.mapper.building.BuildingMapper;
import com.endava.insurance.insurance_service.persistence.repository.BuildingRepository;
import com.endava.insurance.insurance_service.persistence.repository.PolicyRepository;
import com.endava.insurance.insurance_service.application.service.contract.BuildingService;
import com.endava.insurance.insurance_service.application.validator.building.BuildingValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuildingServiceImpl implements BuildingService {

    private static final String BUILDING_NOT_FOUND_MSG = "Building not found with id: ";

    private final BuildingRepository buildingRepository;
    private final PolicyRepository policyRepository;
    private final BuildingMapper buildingMapper;
    private final BuildingValidator buildingValidator;

    @Override
    @Transactional
    public BuildingResponseDTO createBuilding(Long clientId, BuildingRequestDTO request) throws ResourceNotFoundException, ValidationException {
        buildingValidator.validateClientAndCityExist(clientId, request.cityId());

        Building building = buildingMapper.toEntity(request, clientId);
        Building saved = buildingRepository.save(building);
        log.info("Building creation succeeded: id={}, clientId={}", saved.getId(), clientId);
        return buildingMapper.toResponse(saved);
    }

    @Override
    public BuildingResponseDTO getBuildingById(Long buildingId) throws ResourceNotFoundException {
        return buildingRepository.findById(buildingId)
                .map(buildingMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(BUILDING_NOT_FOUND_MSG + buildingId));
    }

    @Override
    public BuildingResponseDTOV2 getBuildingByIdV2(Long buildingId) throws ResourceNotFoundException {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new ResourceNotFoundException(BUILDING_NOT_FOUND_MSG + buildingId));
        var policies = policyRepository.findByBuildingIdOrderByStartDateDesc(buildingId);
        return buildingMapper.toResponseV2(building, policies);
    }

    @Override
    public Page<BuildingResponseDTO> getBuildingsByClientId(Long clientId, Pageable pageable) {
        return buildingRepository.findByOwnerId(clientId, pageable)
                .map(buildingMapper::toResponse);
    }

    @Override
    public Page<BuildingResponseDTOV2> getBuildingsByClientIdV2(Long clientId, Pageable pageable) {
        return buildingRepository.findByOwnerId(clientId, pageable).map(this::toBuildingResponseV2);
    }

    private BuildingResponseDTOV2 toBuildingResponseV2(Building building) {
        var policies = policyRepository.findByBuildingIdOrderByStartDateDesc(building.getId());
        return buildingMapper.toResponseV2(building, policies);
    }

    @Override
    @Transactional
    public BuildingResponseDTO updateBuilding(Long buildingId, BuildingRequestDTO request) throws ResourceNotFoundException, ValidationException {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new ResourceNotFoundException(BUILDING_NOT_FOUND_MSG + buildingId));

        buildingValidator.validateClientAndCityExist(building.getOwner().getId(), request.cityId());

        buildingMapper.updateEntityFromRequest(request, building);

        Building saved = buildingRepository.save(building);
        log.info("Building update succeeded: id={}", saved.getId());
        return buildingMapper.toResponse(saved);
    }
}