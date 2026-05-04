package com.endava.insurance.insurance_service.api.controller.broker;

import com.endava.insurance.insurance_service.application.dto.policy.PolicyCancelDTO;
import com.endava.insurance.insurance_service.application.dto.policy.PolicyCreateDTO;
import com.endava.insurance.insurance_service.application.dto.policy.PolicyResponseDTO;
import com.endava.insurance.insurance_service.application.service.contract.PolicyService;
import com.endava.insurance.insurance_service.domain.enums.PolicyStatus;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v2/brokers/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @GetMapping
    public ResponseEntity<Page<PolicyResponseDTO>> getPolicies(
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Long brokerId,
            @RequestParam(required = false) PolicyStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDateTo,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(policyService.getFiltered(clientId, brokerId, status, startDateFrom, endDateTo, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PolicyResponseDTO> getPolicyById(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(policyService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PolicyResponseDTO> createDraft(@Valid @RequestBody PolicyCreateDTO request)
            throws ResourceNotFoundException, ValidationException {
        return new ResponseEntity<>(policyService.createDraft(request), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<PolicyResponseDTO> activate(@PathVariable Long id)
            throws ResourceNotFoundException, ValidationException {
        return ResponseEntity.ok(policyService.activate(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<PolicyResponseDTO> cancel(
            @PathVariable Long id,
            @Valid @RequestBody PolicyCancelDTO request)
            throws ResourceNotFoundException, ValidationException {
        return ResponseEntity.ok(policyService.cancel(id, request));
    }
}
