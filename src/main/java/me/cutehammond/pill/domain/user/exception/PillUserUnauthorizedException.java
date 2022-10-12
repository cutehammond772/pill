package me.cutehammond.pill.domain.user.exception;

import me.cutehammond.pill.global.exception.ErrorCode;
import me.cutehammond.pill.global.exception.PillException;
import org.springframework.http.HttpStatus;

public final class PillUserUnauthorizedException extends PillException {

    public PillUserUnauthorizedException() {
        super("unauthorized request.", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
    }

    public PillUserUnauthorizedException(String message) {
        super(message, ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
    }

}
