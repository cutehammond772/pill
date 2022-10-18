package me.cutehammond.pill.domain.point.exception;

import lombok.NonNull;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public final class PillPointAccumulationFailedException extends PillPointException {

    public PillPointAccumulationFailedException(@NonNull String message) {
        super(message, ErrorCode.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public PillPointAccumulationFailedException() {
        this("사용자의 포인트 정보를 모으는 데 실패하였습니다.");
    }

}
