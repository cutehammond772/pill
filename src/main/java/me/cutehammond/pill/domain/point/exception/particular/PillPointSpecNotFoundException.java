package me.cutehammond.pill.domain.point.exception.particular;

import lombok.NonNull;
import me.cutehammond.pill.domain.point.exception.PillPointException;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public final class PillPointSpecNotFoundException extends ParticularPillPointException {

    public PillPointSpecNotFoundException(@NonNull String message, @NonNull String pointName) {
        super(message, ErrorCode.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, pointName);
    }

    public PillPointSpecNotFoundException(@NonNull String pointName) {
        this("해당 포인트 명세 [" + pointName + "]는 존재하지 않는 명세입니다.", pointName);
    }
}
