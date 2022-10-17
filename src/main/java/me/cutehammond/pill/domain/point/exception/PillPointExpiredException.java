package me.cutehammond.pill.domain.point.exception;

import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public final class PillPointExpiredException extends PillPointException {

    public PillPointExpiredException(String message) {
        super(message, ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
    }

    public PillPointExpiredException() {
        this("that point is expired.");
    }
}
