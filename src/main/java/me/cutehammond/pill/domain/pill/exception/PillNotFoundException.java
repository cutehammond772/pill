package me.cutehammond.pill.domain.pill.exception;

import lombok.NonNull;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class PillNotFoundException extends PillException {

    public PillNotFoundException(@NonNull String message, @NonNull Long pillId) {
        super(message, ErrorCode.BAD_REQUEST, HttpStatus.NOT_FOUND, pillId);
    }

    public PillNotFoundException(@NonNull Long pillId) {
        this(String.format("존재하지 않는 Pill[pillId=%d]입니다.", pillId), pillId);
    }
}
