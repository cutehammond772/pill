package me.cutehammond.pill.domain.user.exception;

import lombok.Getter;
import lombok.NonNull;
import me.cutehammond.pill.global.exception.ErrorCode;
import me.cutehammond.pill.global.exception.PillException;
import org.springframework.http.HttpStatus;

@Getter
public class PillUserException extends PillException {

    private final String userId;

    public PillUserException(String message, @NonNull ErrorCode errorCode, @NonNull HttpStatus httpStatus, @NonNull String userId) {
        super(message, errorCode, httpStatus);
        this.userId = userId;
    }

}
