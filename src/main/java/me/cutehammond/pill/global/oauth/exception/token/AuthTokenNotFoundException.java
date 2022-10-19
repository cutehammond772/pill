package me.cutehammond.pill.global.oauth.exception.token;

import me.cutehammond.pill.global.exception.ErrorCode;
import me.cutehammond.pill.global.oauth.entity.AuthToken;
import org.springframework.http.HttpStatus;

public final class AuthTokenNotFoundException extends AuthTokenException {

    public AuthTokenNotFoundException(AuthToken.AuthTokenType authTokenType) {
        super("AuthToken " + authTokenType.name() + " is not found.", ErrorCode.BAD_REQUEST, HttpStatus.NOT_FOUND, authTokenType);
    }

}
