package com.endava.insurance.insurance_service.application.service.impl;

import com.endava.insurance.insurance_service.application.dto.auth.LoginRequest;
import com.endava.insurance.insurance_service.application.dto.auth.LoginResponse;
import com.endava.insurance.insurance_service.application.service.contract.AuthService;
import com.endava.insurance.insurance_service.domain.exception.AccountInactiveException;
import com.endava.insurance.insurance_service.domain.exception.InvalidCredentialsException;
import com.endava.insurance.insurance_service.security.AuthUserDetails;
import com.endava.insurance.insurance_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid email or password";

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) throws InvalidCredentialsException, AccountInactiveException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

            AuthUserDetails user = (AuthUserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(authentication);
            List<String> roles = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            return LoginResponse.of(token, user.getUsername(), roles);
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE);
        } catch (InternalAuthenticationServiceException e) {
            if (e.getCause() instanceof AccountInactiveException cause) {
                throw cause;
            }
            throw e;
        }
    }
}
