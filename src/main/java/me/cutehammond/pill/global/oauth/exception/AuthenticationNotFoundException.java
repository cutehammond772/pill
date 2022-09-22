package me.cutehammond.pill.global.oauth.exception;

public class AuthenticationNotFoundException extends RuntimeException {

    public AuthenticationNotFoundException() {
        super("Authentication in SecurityContext is null.");
    }

}
