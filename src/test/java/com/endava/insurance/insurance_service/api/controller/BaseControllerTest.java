package com.endava.insurance.insurance_service.api.controller;

import com.endava.insurance.insurance_service.security.AuthUserDetailsService;
import com.endava.insurance.insurance_service.security.JwtService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base class for WebMvcTest controller tests. Provides required mocks for JWT security
 * so that JwtAuthenticationFilter can be instantiated without loading full application context.
 * Uses "test" profile so TestSecurityConfig applies permitAll for all requests.
 */
@ActiveProfiles("test")
public abstract class BaseControllerTest {

    @MockBean
    protected JwtService jwtService;

    @MockBean
    protected AuthUserDetailsService authUserDetailsService;
}
