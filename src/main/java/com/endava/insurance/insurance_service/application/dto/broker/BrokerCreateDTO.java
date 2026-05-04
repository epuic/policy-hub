package com.endava.insurance.insurance_service.application.dto.broker;

import com.endava.insurance.insurance_service.domain.enums.BrokerStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = false)
public record BrokerCreateDTO(
        @NotBlank(message = "Broker code is required")
        @Size(min = 1, max = 50)
        @JsonProperty("brokerCode")
        String brokerCode,

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 20, message = "Name must be between 2 and 20 characters")
        @JsonProperty("name")
        String name,

        @Email(message = "Invalid email format")
        @JsonProperty("email")
        String email,

        @Pattern(regexp = "^\\+?[0-9.]{10,15}$", message = "Invalid phone number format")
        @JsonProperty("phone")
        String phone,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        @JsonProperty("password")
        String password,

        @NotNull(message = "Status is required")
        @JsonProperty("status")
        BrokerStatus status,

        @DecimalMin(value = "0", message = "Commission percentage must be non-negative")
        @DecimalMax(value = "100", message = "Commission percentage must not exceed 100")
        @JsonProperty("commissionPercentage")
        BigDecimal commissionPercentage
) {}
