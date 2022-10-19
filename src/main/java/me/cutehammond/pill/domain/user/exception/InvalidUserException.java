package me.cutehammond.pill.domain.user.exception;

import lombok.NonNull;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidUserException extends UserException {

    public InvalidUserException(@NonNull String message, @NonNull String userId) {
        super(message, ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, userId);
    }

    public InvalidUserException(@NonNull String userId) {
        this("유효하지 않은 사용자[userId: " + userId + "]입니다.", userId);
    }

}
