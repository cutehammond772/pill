package me.cutehammond.pill.domain.category.exception;

import lombok.Getter;
import lombok.NonNull;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
public class CategoryInvalidPageRequestException extends CategoryException {

    private final Integer page;

    private final Integer size;

    public CategoryInvalidPageRequestException(@NonNull String message, String categoryName, Long categoryId,
                                               @NonNull Integer page, @NonNull Integer size) {
        super(message, ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, categoryName, categoryId);
        this.page = page;
        this.size = size;
    }

    public CategoryInvalidPageRequestException(@NonNull Long categoryId, @NonNull Integer page, @NonNull Integer size) {
        this(String.format("카테고리[categoryId=%d] 페이지를 불러오는 데 실패하였습니다. 유효하지 않은 페이지 설정[page=%d, size=%d]입니다.",
                categoryId, page, size), null, categoryId, page, size);
    }

}
