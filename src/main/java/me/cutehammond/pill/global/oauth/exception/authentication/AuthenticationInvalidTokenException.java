package me.cutehammond.pill.global.oauth.exception.authentication;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationInvalidTokenException extends AuthenticationException {

    public AuthenticationInvalidTokenException() {
        super("error occurred in authentication.");
    }

    public AuthenticationInvalidTokenException(String message) {
        super(message);
    }

}
