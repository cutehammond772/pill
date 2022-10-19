package me.cutehammond.pill.domain.user.exception;

import lombok.NonNull;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public final class UserUnauthorizedException extends UserException {

    public UserUnauthorizedException(@NonNull String message, @NonNull String userId) {
        super(message, ErrorCode.BAD_REQUEST, HttpStatus.UNAUTHORIZED, userId);
    }

    public UserUnauthorizedException(@NonNull String userId) {
        this("사용자 [userId: " + userId + "] 권한에 맞지 않는 요청입니다.", userId);
    }

    public UserUnauthorizedException() {
        this("사용자를 식별할 수 없습니다. 인증(로그인)이 필요합니다.", "Anonymous");
    }

}
