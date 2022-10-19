package me.cutehammond.pill.domain.pill.exception;

import lombok.Getter;
import lombok.NonNull;
import me.cutehammond.pill.global.exception.ErrorCode;
import me.cutehammond.pill.global.exception.GeneralException;
import org.springframework.http.HttpStatus;

@Getter
public class PillException extends GeneralException {

    private final Long pillId;

    public PillException(@NonNull String message, @NonNull ErrorCode errorCode, @NonNull HttpStatus httpStatus,
                         @NonNull Long pillId) {
        super(message, errorCode, httpStatus);
        this.pillId = pillId;
    }

}
