package com.endava.insurance.insurance_service.application.dto.geography;

import com.endava.insurance.insurance_service.application.dto.building.BuildingResponseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;

public record CityWithBuildingsDTO(
        @JsonProperty("id")
        Long id,
        @JsonProperty("name")
        String name,
        @JsonProperty("buildings")
        Page<BuildingResponseDTO> buildings
) {}
