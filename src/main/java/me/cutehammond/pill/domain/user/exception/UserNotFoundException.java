package me.cutehammond.pill.domain.user.exception;

import lombok.NonNull;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public final class UserNotFoundException extends UserException {

    public UserNotFoundException(@NonNull String message, @NonNull String userId) {
        super(message, ErrorCode.BAD_REQUEST, HttpStatus.NOT_FOUND, userId);
    }

    public UserNotFoundException(@NonNull String userId) {
        this("사용자 [userId: " + userId + "]는 존재하지 않습니다.", userId);
    }

}
