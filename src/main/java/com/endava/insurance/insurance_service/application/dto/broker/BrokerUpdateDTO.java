package com.endava.insurance.insurance_service.application.dto.broker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = false)
public record BrokerUpdateDTO(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 20, message = "Name must be between 2 and 200 characters")
        @JsonProperty("name")
        String name,

        @Email(message = "Invalid email format")
        @JsonProperty("email")
        String email,

        @Pattern(regexp = "^\\+?[0-9.]{10,15}$", message = "Invalid phone number format")
        @JsonProperty("phone")
        String phone,

        @DecimalMin(value = "0", message = "Commission percentage must be non-negative")
        @DecimalMax(value = "100", message = "Commission percentage must not exceed 100")
        @JsonProperty("commissionPercentage")
        BigDecimal commissionPercentage
) {}
