package com.endava.insurance.insurance_service.api.controller.auth;

import com.endava.insurance.insurance_service.application.dto.auth.LoginRequest;
import com.endava.insurance.insurance_service.application.dto.auth.LoginResponse;
import com.endava.insurance.insurance_service.application.service.contract.AuthService;
import com.endava.insurance.insurance_service.domain.exception.AccountInactiveException;
import com.endava.insurance.insurance_service.domain.exception.InvalidCredentialsException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) throws InvalidCredentialsException, AccountInactiveException {
        return ResponseEntity.ok(authService.login(request));
    }
}
