package com.endava.insurance.insurance_service.application.service.impl;

import com.endava.insurance.insurance_service.domain.model.Client;
import com.endava.insurance.insurance_service.application.dto.client.ClientCreateDTO;
import com.endava.insurance.insurance_service.application.dto.client.ClientDetailsResponseDTO;
import com.endava.insurance.insurance_service.application.dto.client.ClientResponseDTO;
import com.endava.insurance.insurance_service.application.dto.client.ClientUpdateDTO;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.application.mapper.building.BuildingMapper;
import com.endava.insurance.insurance_service.application.mapper.client.ClientMapper;
import com.endava.insurance.insurance_service.persistence.repository.BuildingRepository;
import com.endava.insurance.insurance_service.persistence.repository.ClientRepository;
import com.endava.insurance.insurance_service.application.service.contract.ClientService;
import com.endava.insurance.insurance_service.application.validator.client.ClientValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final ClientValidator clientValidator;
    private final BuildingRepository buildingRepository;
    private final BuildingMapper buildingMapper;

    @Override
    @Transactional
    public ClientResponseDTO createClient(ClientCreateDTO request) throws ValidationException {
        clientValidator.validateNewClient(request);

        Client client = clientMapper.toEntity(request);

        Client saved = clientRepository.save(client);
        log.info("Client creation succeeded: id={}, name={}", saved.getId(), saved.getName());
        return clientMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ClientResponseDTO updateClient(Long id, ClientUpdateDTO request) throws ResourceNotFoundException, ValidationException {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        clientValidator.validateClientUpdate(client, request);

        clientMapper.updateEntityFromRequest(request, client);

        Client saved = clientRepository.save(client);
        log.info("Client update succeeded: id={}", saved.getId());
        return clientMapper.toResponse(saved);
    }

    @Override
    public ClientDetailsResponseDTO getClientById(Long id, Pageable buildingPageable) throws ResourceNotFoundException {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        var buildings = buildingRepository.findByOwnerId(id, buildingPageable)
                .map(buildingMapper::toResponse);

        return clientMapper.toDetailsResponse(client, buildings);
    }

    @Override
    public Page<ClientResponseDTO> getAllClients(Pageable pageable) {
        return clientRepository.findAll(pageable).map(clientMapper::toResponse);
    }

    @Override
    public Page<ClientResponseDTO> searchClients(String name, String identifier, Pageable pageable) {
        if (identifier != null && !identifier.isBlank()) {
            var found = clientRepository.findByIdentificationNumber(identifier);
            if (found.isPresent()) {
                ClientResponseDTO dto = clientMapper.toResponse(found.get());
                return new PageImpl<>(List.of(dto), pageable, 1);
            }
            return Page.empty(pageable);
        }

        String searchName = (name != null) ? name.trim() : "";
        return clientRepository.findByNameContainingIgnoreCase(searchName, pageable)
                .map(clientMapper::toResponse);
    }
}
