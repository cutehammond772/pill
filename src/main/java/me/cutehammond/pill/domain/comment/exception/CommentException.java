package me.cutehammond.pill.domain.comment.exception;

import lombok.NonNull;
import me.cutehammond.pill.global.exception.ErrorCode;
import me.cutehammond.pill.global.exception.GeneralException;
import org.springframework.http.HttpStatus;

public class CommentException extends GeneralException {

    public CommentException(String message, @NonNull ErrorCode errorCode, @NonNull HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }

}
