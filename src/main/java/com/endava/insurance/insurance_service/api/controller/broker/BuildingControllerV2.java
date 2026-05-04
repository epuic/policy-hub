package com.endava.insurance.insurance_service.api.controller.broker;

import com.endava.insurance.insurance_service.application.dto.building.BuildingResponseDTOV2;
import com.endava.insurance.insurance_service.application.service.contract.BuildingService;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/brokers")
@RequiredArgsConstructor
public class BuildingControllerV2 {

    private final BuildingService buildingService;

    @GetMapping("/clients/{clientId}/buildings")
    public ResponseEntity<Page<BuildingResponseDTOV2>> getClientsBuildings(
            @PathVariable Long clientId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(buildingService.getBuildingsByClientIdV2(clientId, pageable));
    }

    @GetMapping("/buildings/{buildingId}")
    public ResponseEntity<BuildingResponseDTOV2> getBuildingById(@PathVariable Long buildingId) throws ResourceNotFoundException {
        return ResponseEntity.ok(buildingService.getBuildingByIdV2(buildingId));
    }
}
