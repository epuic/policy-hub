package com.endava.insurance.insurance_service.application.service.contract;

import com.endava.insurance.insurance_service.application.dto.building.BuildingRequestDTO;
import com.endava.insurance.insurance_service.application.dto.building.BuildingResponseDTO;
import com.endava.insurance.insurance_service.application.dto.building.BuildingResponseDTOV2;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BuildingService {
    BuildingResponseDTO createBuilding(Long clientId, BuildingRequestDTO request) throws ResourceNotFoundException, ValidationException;

    BuildingResponseDTO getBuildingById(Long buildingId) throws ResourceNotFoundException;

    BuildingResponseDTOV2 getBuildingByIdV2(Long buildingId) throws ResourceNotFoundException;

    Page<BuildingResponseDTO> getBuildingsByClientId(Long clientId, Pageable pageable);

    Page<BuildingResponseDTOV2> getBuildingsByClientIdV2(Long clientId, Pageable pageable);

    BuildingResponseDTO updateBuilding(Long buildingId, BuildingRequestDTO request) throws ResourceNotFoundException, ValidationException;
}