package me.cutehammond.pill.domain.category.exception;

import lombok.NonNull;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends CategoryException {

    public CategoryNotFoundException(@NonNull String message, String categoryName, Long categoryId) {
        super(message, ErrorCode.BAD_REQUEST, HttpStatus.NOT_FOUND, categoryName, categoryId);
    }

    public CategoryNotFoundException(@NonNull String categoryName) {
        this(String.format("카테고리[categoryName=%s]는 존재하지 않습니다.", categoryName), categoryName, -1L);
    }

    public CategoryNotFoundException(@NonNull Long categoryId) {
        this(String.format("카테고리[categoryId=%d]는 존재하지 않습니다.", categoryId), null, categoryId);
    }

}
