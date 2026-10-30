package com.finpulse.account_service.service;


public class AccountException extends RuntimeException {

    public enum Code {
        NOT_FOUND,
        INVALID_CURRENCY
    }

    private final Code code;

    public AccountException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public Code getCode() {
        return code;
    }
}