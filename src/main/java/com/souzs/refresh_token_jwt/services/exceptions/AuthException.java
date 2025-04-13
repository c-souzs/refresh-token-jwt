package com.souzs.refresh_token_jwt.services.exceptions;

public class AuthException extends RuntimeException {
    public AuthException(String msg) {
        super(msg);
    }
}
