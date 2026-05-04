package com.endava.insurance.insurance_service.application.dto.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

@JsonIgnoreProperties(ignoreUnknown = false)
public record ClientUpdateDTO(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 20, message = "Name must be between 2 and 20 characters")
        @JsonProperty("name")
        String name,

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
