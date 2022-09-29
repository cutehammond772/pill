package me.cutehammond.pill.global.oauth.handler.exception;

import org.springframework.security.core.AuthenticationException;

public class TokenValidFailedException extends AuthenticationException {

    public TokenValidFailedException() {
        super("Failed to generate token.");
    }

    // to prevent leaking token
    private TokenValidFailedException(String message) {
        super(message);
    }

}
