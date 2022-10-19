package me.cutehammond.pill.domain.user.exception;

import lombok.NonNull;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class UserAlreadyRegisteredException extends UserException {

    public UserAlreadyRegisteredException(@NonNull String message, @NonNull String userId) {
        super(message, ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, userId);
    }

    public UserAlreadyRegisteredException(@NonNull String userId) {
        this("사용자 [userId: " + userId + "]는 이미 등록된 상태입니다.", userId);
    }

}
