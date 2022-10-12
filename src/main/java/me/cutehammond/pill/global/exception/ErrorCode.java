package me.cutehammond.pill.global.exception;

import lombok.RequiredArgsConstructor;

/**
 * Error의 유형을 나타냅니다. <br><br>
 * 1. 클라이언트 단에서 발생 <-> 서버 단에서 발생 <br>
 * 2. Spring 내부에서 발생 <-> 일반 로직에서 발생 <br>
 * 을 기준으로 하며, 세부적인 오류 정보는 (APIErrorResponse).getSpecificCode()를 참조하세요.
 */
@RequiredArgsConstructor
public enum ErrorCode {

    OK(ErrorCategory.SUCCESS),

    BAD_REQUEST(ErrorCategory.CLIENT_SIDE),
    SPRING_BAD_REQUEST(ErrorCategory.CLIENT_SIDE),

    INTERNAL_ERROR(ErrorCategory.SERVER_SIDE),
    SPRING_INTERNAL_ERROR(ErrorCategory.SERVER_SIDE);

    private final ErrorCategory errorCategory;

    public boolean isServerSideError() {
        return errorCategory == ErrorCategory.SERVER_SIDE;
    }

    public boolean isClientSideError() {
        return errorCategory == ErrorCategory.CLIENT_SIDE;
    }

    private enum ErrorCategory {
        SUCCESS, CLIENT_SIDE, SERVER_SIDE;
    }

}
