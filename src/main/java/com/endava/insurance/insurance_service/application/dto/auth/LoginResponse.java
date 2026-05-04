package com.endava.insurance.insurance_service.application.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record LoginResponse(
        @JsonProperty("token")
        String token,

        @JsonProperty("type")
        String type,

        @JsonProperty("email")
        String email,

        @JsonProperty("roles")
        List<String> roles
) {
    public static LoginResponse of(String token, String email, List<String> roles) {
        return new LoginResponse(token, "Bearer", email, roles);
    }
}
