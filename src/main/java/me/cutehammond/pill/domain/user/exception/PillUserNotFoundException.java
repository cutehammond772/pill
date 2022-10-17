package me.cutehammond.pill.domain.user.exception;

import me.cutehammond.pill.global.exception.ErrorCode;
import me.cutehammond.pill.global.exception.PillException;
import org.springframework.http.HttpStatus;

public final class PillUserNotFoundException extends PillException {

    public PillUserNotFoundException(String message) {
        super(message, ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
    }

    public PillUserNotFoundException() {
        this("that user doesn't exist.");
    }

}
