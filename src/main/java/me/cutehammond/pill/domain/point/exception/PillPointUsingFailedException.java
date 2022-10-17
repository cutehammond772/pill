package me.cutehammond.pill.domain.point.exception;

import me.cutehammond.pill.global.exception.ErrorCode;
import me.cutehammond.pill.global.exception.PillException;
import org.springframework.http.HttpStatus;

public final class PillPointUsingFailedException extends PillException {

    public PillPointUsingFailedException(String message) {
        super(message, ErrorCode.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public PillPointUsingFailedException() {
        this("failed to use points.");
    }

}
