package com.endava.insurance.insurance_service.application.dto.broker;

import com.endava.insurance.insurance_service.domain.enums.BrokerStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record BrokerResponseDTO(
        @JsonProperty("id")
        Long id,
        @JsonProperty("brokerCode")
        String brokerCode,
        @JsonProperty("name")
        String name,
        @JsonProperty("email")
        String email,
        @JsonProperty("phone")
        String phone,
        @JsonProperty("status")
        BrokerStatus status,
        @JsonProperty("commissionPercentage")
        BigDecimal commissionPercentage
) {}
