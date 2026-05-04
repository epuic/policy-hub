package com.endava.insurance.insurance_service.application.service.contract;

import com.endava.insurance.insurance_service.application.dto.auth.LoginRequest;
import com.endava.insurance.insurance_service.application.dto.auth.LoginResponse;
import com.endava.insurance.insurance_service.domain.exception.AccountInactiveException;
import com.endava.insurance.insurance_service.domain.exception.InvalidCredentialsException;

public interface AuthService {

    LoginResponse login(LoginRequest request) throws InvalidCredentialsException, AccountInactiveException;
}
