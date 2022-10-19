package me.cutehammond.pill.global.oauth.exception.token;

import me.cutehammond.pill.global.exception.ErrorCode;
import me.cutehammond.pill.global.oauth.entity.AuthToken;
import org.springframework.http.HttpStatus;

public final class InvalidAuthTokenException extends AuthTokenException {

    public InvalidAuthTokenException(AuthToken.AuthTokenType authTokenType) {
        super("Invalid AuthToken " + authTokenType.name() + ".", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, authTokenType);
    }

    public InvalidAuthTokenException(String message, AuthToken.AuthTokenType authTokenType) {
        super(message, ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, authTokenType);
    }

}
