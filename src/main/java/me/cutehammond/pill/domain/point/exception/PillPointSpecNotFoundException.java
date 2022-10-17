package me.cutehammond.pill.domain.point.exception;

import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public final class PillPointSpecNotFoundException extends PillPointException {

    public PillPointSpecNotFoundException(String message) {
        super(message, ErrorCode.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public PillPointSpecNotFoundException() {
        this("that spec of pillpoint doesn't exist.");
    }
}
