package me.cutehammond.pill.domain.point.exception.particular;

import lombok.NonNull;
import me.cutehammond.pill.global.exception.ErrorCode;
import me.cutehammond.pill.global.exception.PillException;
import org.springframework.http.HttpStatus;

public final class PillPointUsingFailedException extends ParticularPillPointException {

    public PillPointUsingFailedException(@NonNull String message, @NonNull String pointName) {
        super(message, ErrorCode.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, pointName);
    }

    public PillPointUsingFailedException(@NonNull String pointName) {
        this("포인트 [" + pointName + "]를 사용하는 데 실패하였습니다.", pointName);
    }

}
