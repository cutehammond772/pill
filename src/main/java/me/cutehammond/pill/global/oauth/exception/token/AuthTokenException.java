package me.cutehammond.pill.global.oauth.exception.token;

import lombok.Getter;
import me.cutehammond.pill.global.exception.ErrorCode;
import me.cutehammond.pill.global.exception.GeneralException;
import me.cutehammond.pill.global.oauth.entity.AuthToken;
import org.springframework.http.HttpStatus;

@Getter
public class AuthTokenException extends GeneralException {

    private final AuthToken.AuthTokenType authTokenType;

    public AuthTokenException(String message, ErrorCode errorCode, HttpStatus httpStatus, AuthToken.AuthTokenType authTokenType) {
        super(message, errorCode, httpStatus);
        this.authTokenType = authTokenType;
    }

}
