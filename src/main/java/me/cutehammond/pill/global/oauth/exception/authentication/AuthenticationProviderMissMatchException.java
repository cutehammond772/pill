package me.cutehammond.pill.global.oauth.exception.authentication;

import me.cutehammond.pill.global.exception.ErrorCode;
import me.cutehammond.pill.global.exception.PillException;
import org.springframework.http.HttpStatus;

public class AuthenticationProviderMissMatchException extends PillException {

    public AuthenticationProviderMissMatchException(String message) {
        super(message, ErrorCode.SPRING_BAD_REQUEST, HttpStatus.BAD_REQUEST);
    }

}
