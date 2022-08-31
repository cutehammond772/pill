package me.cutehammond.pill.global.oauth.exception;

public class TokenValidFailedException extends RuntimeException {

    public TokenValidFailedException() {
        super("Failed to generate token.");
    }

    // to prevent leaking token
    private TokenValidFailedException(String message) {
        super(message);
    }

}
