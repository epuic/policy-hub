package com.endava.insurance.insurance_service.api.controller.broker;

import com.endava.insurance.insurance_service.application.dto.client.ClientCreateDTO;
import com.endava.insurance.insurance_service.application.dto.client.ClientDetailsResponseDTO;
import com.endava.insurance.insurance_service.application.dto.client.ClientResponseDTO;
import com.endava.insurance.insurance_service.application.dto.client.ClientUpdateDTO;
import com.endava.insurance.insurance_service.application.service.contract.ClientService;
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
@RequestMapping("/api/brokers/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponseDTO> createClient(@Valid @RequestBody ClientCreateDTO request) throws ValidationException {
        return new ResponseEntity<>(clientService.createClient(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientUpdateDTO request) throws ResourceNotFoundException, ValidationException {
        return ResponseEntity.ok(clientService.updateClient(id, request));
    }

    @GetMapping
    public ResponseEntity<Page<ClientResponseDTO>> getAllClients(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(clientService.getAllClients(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDetailsResponseDTO> getClientById(
            @PathVariable Long id,
            @PageableDefault(size = 10) Pageable buildingPageable) throws ResourceNotFoundException {
        return ResponseEntity.ok(clientService.getClientById(id, buildingPageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ClientResponseDTO>> searchClients(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String identifier,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(clientService.searchClients(name, identifier, pageable));
    }
}