package com.endava.insurance.insurance_service.application.dto.geography;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GeographyResponseDTO(
        @JsonProperty("id")
        Long id,
        @JsonProperty("name")
        String name
) {}