package com.endava.insurance.insurance_service.api.controller.admin;

import com.endava.insurance.insurance_service.application.dto.ai.AiClusterAssignmentResponseDTO;
import com.endava.insurance.insurance_service.application.dto.ai.AiClusterAnalyticsDTO;
import com.endava.insurance.insurance_service.application.dto.ai.AiClusterConfigurationRequestDTO;
import com.endava.insurance.insurance_service.application.dto.ai.AiClusterConfigurationResponseDTO;
import com.endava.insurance.insurance_service.application.dto.ai.AiClusteringRunResponseDTO;
import com.endava.insurance.insurance_service.application.service.ai.AiSegmentationService;
import com.endava.insurance.insurance_service.domain.enums.AiClusterTarget;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/admin/ai")
@RequiredArgsConstructor
public class AiSegmentationController {

    private final AiSegmentationService aiSegmentationService;

    @PostMapping("/clusters/buildings/run")
    public ResponseEntity<AiClusteringRunResponseDTO> runBuildingClustering(
            @RequestParam(defaultValue = "4") int k
    ) throws ValidationException {
        return ResponseEntity.ok(aiSegmentationService.runBuildingClustering(k));
    }

    @PostMapping("/clusters/clients/run")
    public ResponseEntity<AiClusteringRunResponseDTO> runClientClustering(
            @RequestParam(defaultValue = "4") int k
    ) throws ValidationException {
        return ResponseEntity.ok(aiSegmentationService.runClientClustering(k));
    }

    @GetMapping("/clusters/buildings")
    public ResponseEntity<List<AiClusterAssignmentResponseDTO>> getBuildingAssignments() {
        return ResponseEntity.ok(aiSegmentationService.getAssignments(AiClusterTarget.BUILDING));
    }

    @GetMapping("/clusters/clients")
    public ResponseEntity<List<AiClusterAssignmentResponseDTO>> getClientAssignments() {
        return ResponseEntity.ok(aiSegmentationService.getAssignments(AiClusterTarget.CLIENT));
    }

    @GetMapping("/clusters/buildings/configurations")
    public ResponseEntity<List<AiClusterConfigurationResponseDTO>> getBuildingConfigurations() {
        return ResponseEntity.ok(aiSegmentationService.getConfigurations(AiClusterTarget.BUILDING));
    }

    @GetMapping("/clusters/buildings/analytics")
    public ResponseEntity<List<AiClusterAnalyticsDTO>> getBuildingAnalytics() {
        return ResponseEntity.ok(aiSegmentationService.getAnalytics(AiClusterTarget.BUILDING));
    }

    @GetMapping("/clusters/clients/configurations")
    public ResponseEntity<List<AiClusterConfigurationResponseDTO>> getClientConfigurations() {
        return ResponseEntity.ok(aiSegmentationService.getConfigurations(AiClusterTarget.CLIENT));
    }

    @GetMapping("/clusters/clients/analytics")
    public ResponseEntity<List<AiClusterAnalyticsDTO>> getClientAnalytics() {
        return ResponseEntity.ok(aiSegmentationService.getAnalytics(AiClusterTarget.CLIENT));
    }

    @PutMapping("/clusters/buildings/configurations/{clusterId}")
    public ResponseEntity<AiClusterConfigurationResponseDTO> updateBuildingConfiguration(
            @PathVariable Integer clusterId,
            @Valid @RequestBody AiClusterConfigurationRequestDTO request
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(aiSegmentationService.updateConfiguration(AiClusterTarget.BUILDING, clusterId, request));
    }

    @PutMapping("/clusters/clients/configurations/{clusterId}")
    public ResponseEntity<AiClusterConfigurationResponseDTO> updateClientConfiguration(
            @PathVariable Integer clusterId,
            @Valid @RequestBody AiClusterConfigurationRequestDTO request
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(aiSegmentationService.updateConfiguration(AiClusterTarget.CLIENT, clusterId, request));
    }
}
