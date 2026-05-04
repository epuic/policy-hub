package com.endava.insurance.insurance_service.application.dto.policy;

import com.endava.insurance.insurance_service.domain.enums.PolicyStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PolicySummaryDTO(
        @JsonProperty("id")
        Long id,

        @JsonProperty("policyNumber")
        String policyNumber,

        @JsonProperty("status")
        PolicyStatus status,

        @JsonProperty("startDate")
        LocalDate startDate,

        @JsonProperty("endDate")
        LocalDate endDate,

        @JsonProperty("finalPremium")
        BigDecimal finalPremium,

        @JsonProperty("currencyCode")
        String currencyCode,

        @JsonProperty("createdAt")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDateTime createdAt
) {}
