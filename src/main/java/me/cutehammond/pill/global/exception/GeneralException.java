package me.cutehammond.pill.global.exception;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

/**
 * Pill 내부에서 발생하는 Exception의 Superclass입니다. <br>
 * 이는 RuntimeException을 상속하므로 Unchecked Exception이며, Transaction에서 이 예외가 호출되면 Rollback됩니다.
 */
@Getter
public class GeneralException extends RuntimeException {

    private final ErrorCode errorCode;
    private final HttpStatus httpStatus;

    public GeneralException(String message, @NonNull ErrorCode errorCode, @NonNull HttpStatus httpStatus) {
        super(message == null ? "Exception occurred: " + errorCode.name() : message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;

        validateError();
    }

    private void validateError() {
        if ((httpStatus.is4xxClientError() && errorCode.isServerSideError()))
            throw new ExceptionStatusMismatchException(errorCode, httpStatus);

        if ((httpStatus.is5xxServerError() && errorCode.isClientSideError()))
            throw new ExceptionStatusMismatchException(errorCode, httpStatus);
    }

    public static class ExceptionStatusMismatchException extends RuntimeException {
        public ExceptionStatusMismatchException(ErrorCode errorCode, HttpStatus httpStatus) {
            super("ErrorCode and HttpStatus mismatch; " + errorCode.name() + " != " + httpStatus.name());
        }
    }

}
