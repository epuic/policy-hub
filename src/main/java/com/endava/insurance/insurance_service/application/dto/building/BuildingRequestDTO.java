package com.endava.insurance.insurance_service.application.dto.building;

import com.endava.insurance.insurance_service.domain.enums.BuildingType;
import com.endava.insurance.insurance_service.domain.enums.RiskFactorType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = false)
public record BuildingRequestDTO(
        @NotBlank(message = "Street is required")
        @JsonProperty("street")
        String street,

        @NotBlank(message = "Number is required")
        @JsonProperty("number")
        String number,

        @NotNull(message = "City ID is required")
        @JsonProperty("cityId")
        Long cityId,

        @NotNull(message = "Construction year is required")
        @Min(value = 1800, message = "Construction year must be at least 1800")
        @JsonProperty("constructionYear")
        Integer constructionYear,

        @NotNull(message = "Building type is required")
        @JsonProperty("type")
        BuildingType type,

        @NotNull(message = "Number of floors is required")
        @Positive(message = "Number of floors must be positive")
        @JsonProperty("numberOfFloors")
        Integer numberOfFloors,

        @NotNull(message = "Surface area is required")
        @Positive(message = "Surface area must be greater than 0")
        @JsonProperty("surfaceArea")
        Double surfaceArea,

        @NotNull(message = "Insured value is required")
        @Positive(message = "Insured value must be greater than 0")
        @JsonProperty("insuredValue")
        Double insuredValue,

        @JsonProperty("riskFactorTypes")
        List<RiskFactorType> riskFactorTypes
) {}