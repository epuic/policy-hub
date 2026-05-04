package com.endava.insurance.insurance_service.application.service.contract;

import com.endava.insurance.insurance_service.application.dto.client.ClientCreateDTO;
import com.endava.insurance.insurance_service.application.dto.client.ClientDetailsResponseDTO;
import com.endava.insurance.insurance_service.application.dto.client.ClientResponseDTO;
import com.endava.insurance.insurance_service.application.dto.client.ClientUpdateDTO;
import com.endava.insurance.insurance_service.domain.exception.ResourceNotFoundException;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {
    ClientResponseDTO createClient(ClientCreateDTO request) throws ValidationException;
    ClientResponseDTO updateClient(Long id, ClientUpdateDTO request) throws ResourceNotFoundException, ValidationException;
    ClientDetailsResponseDTO getClientById(Long id, Pageable buildingPageable) throws ResourceNotFoundException;
    Page<ClientResponseDTO> getAllClients(Pageable pageable);
    Page<ClientResponseDTO> searchClients(String name, String identifier, Pageable pageable);
}