package me.cutehammond.pill.domain.point.exception.particular;

import lombok.NonNull;
import me.cutehammond.pill.domain.point.exception.PillPointException;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PillPointSpecCreatingFailedException extends ParticularPillPointException {

    public PillPointSpecCreatingFailedException(@NonNull String message, @NonNull String pointName) {
        super(message, ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, pointName);
    }

}
