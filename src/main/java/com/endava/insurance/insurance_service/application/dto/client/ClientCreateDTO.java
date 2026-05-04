package com.endava.insurance.insurance_service.application.dto.client;

import com.endava.insurance.insurance_service.domain.enums.ClientType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

@JsonIgnoreProperties(ignoreUnknown = false)
public record ClientCreateDTO(
        @NotBlank(message = "Country code is required (e.g. RO, DE)")
        @Size(min = 2, max = 3, message = "Country code must be 2 or 3 characters")
        @JsonProperty("countryCode")
        String countryCode,

        @NotNull(message = "Client type is required")
        @JsonProperty("type")
        ClientType type,

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 20, message = "Name must be between 2 and 20 characters")
        @JsonProperty("name")
        String name,

        @NotBlank(message = "Identification number is required (CNP for INDIVIDUAL, CUI for COMPANY)")
        @JsonProperty("identificationNumber")
        String identificationNumber,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @JsonProperty("email")
        String email,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+?[0-9.]{10,15}$", message = "Invalid phone number format")
        @JsonProperty("phone")
        String phone,

        @JsonProperty("address")
        String address
) {}
