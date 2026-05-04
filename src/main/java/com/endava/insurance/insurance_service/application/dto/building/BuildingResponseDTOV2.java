package com.endava.insurance.insurance_service.application.dto.building;

import com.endava.insurance.insurance_service.application.dto.policy.PolicySummaryDTO;
import com.endava.insurance.insurance_service.domain.enums.BuildingType;
import com.endava.insurance.insurance_service.domain.enums.RiskFactorType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record BuildingResponseDTOV2(
        @JsonProperty("id")
        Long id,

        @JsonProperty("clientId")
        Long clientId,

        @JsonProperty("clientName")
        String clientName,

        @JsonProperty("fullAddress")
        String fullAddress,

        @JsonProperty("cityName")
        String cityName,

        @JsonProperty("countyName")
        String countyName,

        @JsonProperty("countryName")
        String countryName,

        @JsonProperty("constructionYear")
        Integer constructionYear,

        @JsonProperty("type")
        BuildingType type,

        @JsonProperty("numberOfFloors")
        Integer numberOfFloors,

        @JsonProperty("surfaceArea")
        Double surfaceArea,

        @JsonProperty("insuredValue")
        Double insuredValue,

        @JsonProperty("riskFactorTypes")
        List<RiskFactorType> riskFactorTypes,

        @JsonProperty("policies")
        List<PolicySummaryDTO> policies
) {}
