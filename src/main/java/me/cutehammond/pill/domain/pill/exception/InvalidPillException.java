package me.cutehammond.pill.domain.pill.exception;

import lombok.NonNull;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidPillException extends PillException {

    public InvalidPillException(@NonNull String message, @NonNull Long pillId) {
        super(message, ErrorCode.BAD_REQUEST, HttpStatus.NOT_FOUND, pillId);
    }

    public InvalidPillException(@NonNull Long pillId) {
        this(String.format("유효하지 않은 Pill[pillId=%d]입니다.", pillId), pillId);
    }
}
