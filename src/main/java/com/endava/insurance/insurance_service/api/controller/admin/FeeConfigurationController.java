package com.endava.insurance.insurance_service.api.controller.admin;

import com.endava.insurance.insurance_service.application.dto.metadata.FeeConfigurationRequestDTO;
import com.endava.insurance.insurance_service.application.dto.metadata.FeeConfigurationResponseDTO;
import com.endava.insurance.insurance_service.application.service.contract.FeeConfigurationService;
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
@RequestMapping("/api/v2/admin/fees")
@RequiredArgsConstructor
public class FeeConfigurationController {

    private final FeeConfigurationService feeConfigurationService;

    @PostMapping
    public ResponseEntity<FeeConfigurationResponseDTO> create(@Valid @RequestBody FeeConfigurationRequestDTO request) throws ValidationException {
        return new ResponseEntity<>(feeConfigurationService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeeConfigurationResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody FeeConfigurationRequestDTO request) throws ResourceNotFoundException, ValidationException {
        return ResponseEntity.ok(feeConfigurationService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeeConfigurationResponseDTO> getById(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(feeConfigurationService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<FeeConfigurationResponseDTO>> getAll(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(feeConfigurationService.getAll(pageable));
    }
}
