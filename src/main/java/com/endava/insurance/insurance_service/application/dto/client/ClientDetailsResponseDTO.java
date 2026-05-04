package com.endava.insurance.insurance_service.application.dto.client;

import com.endava.insurance.insurance_service.domain.enums.ClientType;
import com.endava.insurance.insurance_service.application.dto.building.BuildingResponseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;

public record ClientDetailsResponseDTO(
        @JsonProperty("id")
        Long id,
        @JsonProperty("countryCode")
        String countryCode,
        @JsonProperty("type")
        ClientType type,
        @JsonProperty("name")
        String name,
        @JsonProperty("identificationNumber")
        String identificationNumber,
        @JsonProperty("email")
        String email,
        @JsonProperty("phone")
        String phone,
        @JsonProperty("address")
        String address,
        @JsonProperty("buildings")
        Page<BuildingResponseDTO> buildings
) {
}
