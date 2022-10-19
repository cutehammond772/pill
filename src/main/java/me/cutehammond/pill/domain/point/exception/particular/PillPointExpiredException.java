package me.cutehammond.pill.domain.point.exception.particular;

import lombok.NonNull;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public final class PillPointExpiredException extends ParticularPillPointException {

    public PillPointExpiredException(@NonNull String message, @NonNull String pointName) {
        super(message, ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, pointName);
    }

    public PillPointExpiredException(@NonNull String pointName) {
        this(String.format("포인트[pointName=%s]는 만료되어 사용할 수 없습니다.", pointName), pointName);
    }
}
