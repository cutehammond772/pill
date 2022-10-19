package me.cutehammond.pill.domain.point.exception.particular;

import lombok.NonNull;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public final class PillPointUsingFailedException extends ParticularPillPointException {

    public PillPointUsingFailedException(@NonNull String message, @NonNull String pointName) {
        super(message, ErrorCode.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, pointName);
    }

    public PillPointUsingFailedException(@NonNull String pointName) {
        this(String.format("포인트[pointName=%s]를 사용하는 데 실패하였습니다.", pointName), pointName);
    }

}
