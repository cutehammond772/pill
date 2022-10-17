package me.cutehammond.pill.domain.point.exception;

import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public final class PillPointAddingFailedException extends PillPointException {

    public PillPointAddingFailedException(String message) {
        super(message, ErrorCode.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public PillPointAddingFailedException() {
        this("failed adding points.");
    }

}
