package me.cutehammond.pill.domain.category.exception;

import lombok.NonNull;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidCategoryException extends CategoryException {

    public InvalidCategoryException(@NonNull String message, String categoryName, Long categoryId) {
        super(message, ErrorCode.BAD_REQUEST, HttpStatus.NOT_FOUND, categoryName, categoryId);
    }

    public InvalidCategoryException(@NonNull Long categoryId) {
        this(String.format("유효하지 않은 카테고리[categoryId=%d]입니다.", categoryId), null, categoryId);
    }

    public InvalidCategoryException(@NonNull String categoryName) {
        this(String.format("유효하지 않은 카테고리[categoryName=%s]입니다.", categoryName), categoryName, -1L);
    }
}
