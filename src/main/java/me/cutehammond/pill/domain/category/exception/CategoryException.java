package me.cutehammond.pill.domain.category.exception;

import lombok.Getter;
import lombok.NonNull;
import me.cutehammond.pill.global.exception.ErrorCode;
import me.cutehammond.pill.global.exception.GeneralException;
import org.springframework.http.HttpStatus;

@Getter
public class CategoryException extends GeneralException {

    private final String categoryName;

    private final Long categoryId;

    public CategoryException(@NonNull String message, @NonNull ErrorCode errorCode, @NonNull HttpStatus httpStatus,
                             String categoryName, Long categoryId) {
        super(message, errorCode, httpStatus);
        this.categoryName = categoryName;
        this.categoryId = categoryId;
    }

}
