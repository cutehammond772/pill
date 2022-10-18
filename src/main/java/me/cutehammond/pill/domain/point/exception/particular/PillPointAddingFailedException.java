package me.cutehammond.pill.domain.point.exception.particular;

import lombok.NonNull;
import me.cutehammond.pill.domain.point.exception.PillPointException;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public final class PillPointAddingFailedException extends ParticularPillPointException {

    public PillPointAddingFailedException(@NonNull String message, @NonNull String pointName) {
        super(message, ErrorCode.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, pointName);
    }

    public PillPointAddingFailedException(@NonNull String pointName) {
        this("포인트 [" + pointName + "]를 적립하는 데 실패하였습니다.", pointName);
    }

}
