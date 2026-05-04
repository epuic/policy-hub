package com.endava.insurance.insurance_service.domain.exception;


public class AccountInactiveException extends RuntimeException {

    public AccountInactiveException(String message) {
        super(message);
    }
}
