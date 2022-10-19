package me.cutehammond.pill.domain.comment.exception;

import lombok.Getter;
import lombok.NonNull;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
public class CommentInvalidPageRequestException extends CommentException {

    private final Integer page;

    private final Integer size;

    public CommentInvalidPageRequestException(@NonNull String message, @NonNull Integer page, @NonNull Integer size) {
        super(message, ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        this.page = page;
        this.size = size;
    }

    public CommentInvalidPageRequestException(@NonNull Integer page, @NonNull Integer size) {
        this(String.format("댓글 페이지를 불러오는 데 실패하였습니다. 유효하지 않은 페이지 설정[page=%d, size=%d]입니다.",
                page, size), page, size);
    }

}
