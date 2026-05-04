package com.endava.insurance.insurance_service.application.dto.client;

import com.endava.insurance.insurance_service.domain.enums.ClientType;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ClientResponseDTO(
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
        String address
) {}
