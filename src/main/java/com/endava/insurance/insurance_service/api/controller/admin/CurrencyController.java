package com.endava.insurance.insurance_service.api.controller.admin;

import com.endava.insurance.insurance_service.application.dto.metadata.CurrencyRequestDTO;
import com.endava.insurance.insurance_service.application.dto.metadata.CurrencyResponseDTO;
import com.endava.insurance.insurance_service.application.service.contract.CurrencyService;
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
@RequestMapping("/api/v2/admin/currencies")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @PostMapping
    public ResponseEntity<CurrencyResponseDTO> create(@Valid @RequestBody CurrencyRequestDTO request) throws ValidationException {
        return new ResponseEntity<>(currencyService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CurrencyResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CurrencyRequestDTO request) throws ResourceNotFoundException, ValidationException {
        return ResponseEntity.ok(currencyService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CurrencyResponseDTO> getById(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(currencyService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<CurrencyResponseDTO>> getAll(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(currencyService.getAll(pageable));
    }
}
