package com.endava.insurance.insurance_service.application.dto.metadata;

import com.endava.insurance.insurance_service.domain.enums.FeeType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = false)
public record FeeConfigurationRequestDTO(
        @NotBlank(message = "Name is required")
        @Size(max = 200)
        @JsonProperty("name")
        String name,

        @NotNull(message = "Fee type is required")
        @JsonProperty("type")
        FeeType type,

        @NotNull(message = "Percentage is required")
        @DecimalMin(value = "0", message = "Percentage must be >= 0")
        @DecimalMax(value = "100", message = "Percentage must be <= 100")
        @JsonProperty("percentage")
        BigDecimal percentage,

        @JsonProperty("effectiveFrom")
        LocalDate effectiveFrom,

        @JsonProperty("effectiveTo")
        LocalDate effectiveTo,

        @JsonProperty("active")
        boolean active
) {}
