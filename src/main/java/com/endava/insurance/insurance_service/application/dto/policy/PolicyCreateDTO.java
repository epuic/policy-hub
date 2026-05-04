package com.endava.insurance.insurance_service.application.dto.policy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = false)
public record PolicyCreateDTO(
        @NotNull(message = "Client ID is required")
        @JsonProperty("clientId")
        Long clientId,

        @NotNull(message = "Building ID is required")
        @JsonProperty("buildingId")
        Long buildingId,

        @NotNull(message = "Broker ID is required")
        @JsonProperty("brokerId")
        Long brokerId,

        @NotNull(message = "Start date is required")
        @JsonProperty("startDate")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        @JsonProperty("endDate")
        LocalDate endDate,

        @NotNull(message = "Base premium amount is required")
        @DecimalMin(value = "0.000001", message = "Base premium amount must be greater than 0")
        @JsonProperty("basePremiumAmount")
        BigDecimal basePremiumAmount,

        @NotNull(message = "Currency ID is required")
        @JsonProperty("currencyId")
        Long currencyId
) {
}
