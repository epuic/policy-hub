package com.endava.insurance.insurance_service.application.dto.policy;

import com.endava.insurance.insurance_service.domain.enums.PolicyStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PolicyResponseDTO(
        @JsonProperty("id")
        Long id,

        @JsonProperty("policyNumber")
        String policyNumber,

        @JsonProperty("clientId")
        Long clientId,

        @JsonProperty("clientName")
        String clientName,

        @JsonProperty("buildingId")
        Long buildingId,

        @JsonProperty("buildingAddress")
        String buildingAddress,

        @JsonProperty("cityName")
        String cityName,

        @JsonProperty("countyName")
        String countyName,

        @JsonProperty("countryName")
        String countryName,

        @JsonProperty("brokerId")
        Long brokerId,

        @JsonProperty("brokerName")
        String brokerName,

        @JsonProperty("status")
        PolicyStatus status,

        @JsonProperty("startDate")
        LocalDate startDate,

        @JsonProperty("endDate")
        LocalDate endDate,

        @JsonProperty("basePremiumAmount")
        BigDecimal basePremiumAmount,

        @JsonProperty("currencyCode")
        String currencyCode,

        @JsonProperty("finalPremium")
        BigDecimal finalPremium,

        @JsonProperty("createdAt")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDateTime createdAt,

        @JsonProperty("lastUpdatedAt")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDateTime lastUpdatedAt,

        @JsonProperty("cancellationDate")
        LocalDate cancellationDate,

        @JsonProperty("cancellationReason")
        String cancellationReason,

        @JsonProperty("premiumAdjustments")
        List<PremiumAdjustmentDTO> premiumAdjustments
) {
        public PolicyResponseDTO(
                Long id,
                String policyNumber,
                Long clientId,
                String clientName,
                Long buildingId,
                String buildingAddress,
                String cityName,
                String countyName,
                String countryName,
                Long brokerId,
                String brokerName,
                PolicyStatus status,
                LocalDate startDate,
                LocalDate endDate,
                BigDecimal basePremiumAmount,
                String currencyCode,
                BigDecimal finalPremium,
                LocalDateTime createdAt,
                LocalDateTime lastUpdatedAt,
                LocalDate cancellationDate,
                String cancellationReason
        ) {
                this(
                        id,
                        policyNumber,
                        clientId,
                        clientName,
                        buildingId,
                        buildingAddress,
                        cityName,
                        countyName,
                        countryName,
                        brokerId,
                        brokerName,
                        status,
                        startDate,
                        endDate,
                        basePremiumAmount,
                        currencyCode,
                        finalPremium,
                        createdAt,
                        lastUpdatedAt,
                        cancellationDate,
                        cancellationReason,
                        List.of()
                );
        }
}
