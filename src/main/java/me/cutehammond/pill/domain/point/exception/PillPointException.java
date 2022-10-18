package me.cutehammond.pill.domain.point.exception;

import lombok.NonNull;
import me.cutehammond.pill.global.exception.ErrorCode;
import me.cutehammond.pill.global.exception.PillException;
import org.springframework.http.HttpStatus;

public class PillPointException extends PillException {

    public PillPointException(@NonNull String message, @NonNull ErrorCode errorCode, @NonNull HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }

}
