package me.cutehammond.pill.domain.point.exception;

import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public final class PillPointAccumulationFailedException extends PillPointException {

    public PillPointAccumulationFailedException(String message) {
        super(message, ErrorCode.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public PillPointAccumulationFailedException() {
        this("Failed to accumulate user's points.");
    }

}
