package me.cutehammond.pill.domain.point.exception;

import lombok.NonNull;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class PillPointOutOfBoundsException extends PillPointException {

    public PillPointOutOfBoundsException(@NonNull String message) {
        super(message, ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
    }

}
