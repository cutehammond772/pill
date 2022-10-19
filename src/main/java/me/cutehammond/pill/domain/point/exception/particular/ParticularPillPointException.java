package me.cutehammond.pill.domain.point.exception.particular;

import lombok.Getter;
import lombok.NonNull;
import me.cutehammond.pill.domain.point.exception.PillPointException;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
public class ParticularPillPointException extends PillPointException {

    private final String pointName;

    public ParticularPillPointException(String message, @NonNull ErrorCode errorCode, @NonNull HttpStatus httpStatus, @NonNull String pointName) {
        super(message, errorCode, httpStatus);

        this.pointName = pointName;
    }

}
