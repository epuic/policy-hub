package com.endava.insurance.insurance_service.api.controller.admin;

import com.endava.insurance.insurance_service.application.dto.metadata.RiskFactorConfigurationRequestDTO;
import com.endava.insurance.insurance_service.application.dto.metadata.RiskFactorConfigurationResponseDTO;
import com.endava.insurance.insurance_service.application.service.contract.RiskFactorConfigurationService;
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
@RequestMapping("/api/v2/admin/risk-factors")
@RequiredArgsConstructor
public class RiskFactorConfigurationController {

    private final RiskFactorConfigurationService riskFactorConfigurationService;

    @PostMapping
    public ResponseEntity<RiskFactorConfigurationResponseDTO> create(@Valid @RequestBody RiskFactorConfigurationRequestDTO request) throws ValidationException {
        return new ResponseEntity<>(riskFactorConfigurationService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RiskFactorConfigurationResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody RiskFactorConfigurationRequestDTO request) throws ResourceNotFoundException, ValidationException {
        return ResponseEntity.ok(riskFactorConfigurationService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RiskFactorConfigurationResponseDTO> getById(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(riskFactorConfigurationService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<RiskFactorConfigurationResponseDTO>> getAll(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(riskFactorConfigurationService.getAll(pageable));
    }
}
