package com.endava.insurance.insurance_service.application.dto.report;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record PolicyReportByCityDTO(
        @JsonProperty("countryName")
        String countryName,

        @JsonProperty("countyName")
        String countyName,

        @JsonProperty("cityName")
        String cityName,

        @JsonProperty("currencyCode")
        String currencyCode,

        @JsonProperty("policyCount")
        long policyCount,

        @JsonProperty("totalFinalPremium")
        BigDecimal totalFinalPremium,

        @JsonProperty("totalFinalPremiumInBaseCurrency")
        BigDecimal totalFinalPremiumInBaseCurrency
) {
}
