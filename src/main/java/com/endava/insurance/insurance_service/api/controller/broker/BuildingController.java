package com.endava.insurance.insurance_service.api.controller.broker;

import com.endava.insurance.insurance_service.application.dto.building.BuildingRequestDTO;
import com.endava.insurance.insurance_service.application.dto.building.BuildingResponseDTO;
import com.endava.insurance.insurance_service.application.service.contract.BuildingService;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/brokers")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @PostMapping("/clients/{clientId}/buildings")
    public ResponseEntity<BuildingResponseDTO> createBuilding(
            @PathVariable Long clientId,
            @Valid @RequestBody BuildingRequestDTO request) throws ResourceNotFoundException, ValidationException {
        return new ResponseEntity<>(buildingService.createBuilding(clientId, request), HttpStatus.CREATED);
    }

    @GetMapping("/clients/{clientId}/buildings")
    public ResponseEntity<Page<BuildingResponseDTO>> getClientsBuildings(
            @PathVariable Long clientId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(buildingService.getBuildingsByClientId(clientId, pageable));
    }

    @GetMapping("/buildings/{buildingId}")
    public ResponseEntity<BuildingResponseDTO> getBuildingById(@PathVariable Long buildingId) throws ResourceNotFoundException {
        return ResponseEntity.ok(buildingService.getBuildingById(buildingId));
    }

    @PutMapping("/buildings/{buildingId}")
    public ResponseEntity<BuildingResponseDTO> updateBuilding(
            @PathVariable Long buildingId,
            @Valid @RequestBody BuildingRequestDTO request) throws ResourceNotFoundException, ValidationException {
        return ResponseEntity.ok(buildingService.updateBuilding(buildingId, request));
    }
}