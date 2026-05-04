package com.endava.insurance.insurance_service.application.mapper.client;

import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.Client;
import com.endava.insurance.insurance_service.application.dto.building.BuildingResponseDTO;
import com.endava.insurance.insurance_service.application.dto.client.ClientCreateDTO;
import com.endava.insurance.insurance_service.application.dto.client.ClientDetailsResponseDTO;
import com.endava.insurance.insurance_service.application.dto.client.ClientResponseDTO;
import com.endava.insurance.insurance_service.application.dto.client.ClientUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {
    public Client toEntity(ClientCreateDTO request) throws ValidationException {
        return new Client(
                request.countryCode(),
                request.type(),
                request.name(),
                request.identificationNumber(),
                request.email(),
                request.phone(),
                request.address()
        );
    }

    public ClientResponseDTO toResponse(Client client) {
        return new ClientResponseDTO(
                client.getId(),
                client.getCountryCode(),
                client.getType(),
                client.getName(),
                client.getIdentificationNumber(),
                client.getEmail(),
                client.getPhone(),
                client.getAddress()
        );
    }

    public ClientDetailsResponseDTO toDetailsResponse(Client client, Page<BuildingResponseDTO> buildings) {
        return new ClientDetailsResponseDTO(
                client.getId(),
                client.getCountryCode(),
                client.getType(),
                client.getName(),
                client.getIdentificationNumber(),
                client.getEmail(),
                client.getPhone(),
                client.getAddress(),
                buildings
        );
    }


    public void updateEntityFromRequest(ClientUpdateDTO request, Client client) throws ValidationException {
        client.updateDetails(request.name(), request.email(), request.phone(), request.address());
    }
}
