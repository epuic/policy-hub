package com.endava.insurance.insurance_service.api.controller.admin;

import com.endava.insurance.insurance_service.application.dto.broker.BrokerCreateDTO;
import com.endava.insurance.insurance_service.application.dto.broker.BrokerResponseDTO;
import com.endava.insurance.insurance_service.application.dto.broker.BrokerUpdateDTO;
import com.endava.insurance.insurance_service.application.service.contract.BrokerService;
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
@RequestMapping("/api/v2/admin/brokers")
@RequiredArgsConstructor
public class BrokerController {

    private final BrokerService brokerService;

    @PostMapping
    public ResponseEntity<BrokerResponseDTO> create(@Valid @RequestBody BrokerCreateDTO request) throws ValidationException {
        return new ResponseEntity<>(brokerService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrokerResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody BrokerUpdateDTO request) throws ResourceNotFoundException, ValidationException {
        return ResponseEntity.ok(brokerService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrokerResponseDTO> getById(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(brokerService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<BrokerResponseDTO>> getAll(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(brokerService.getAll(pageable));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<BrokerResponseDTO> activate(@PathVariable Long id)
            throws ResourceNotFoundException, ValidationException {
        return ResponseEntity.ok(brokerService.activate(id));
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<BrokerResponseDTO> deactivate(@PathVariable Long id)
            throws ResourceNotFoundException, ValidationException {
        return ResponseEntity.ok(brokerService.deactivate(id));
    }
}
