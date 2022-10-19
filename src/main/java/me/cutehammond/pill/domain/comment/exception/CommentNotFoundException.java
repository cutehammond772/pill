package me.cutehammond.pill.domain.comment.exception;

import lombok.Getter;
import lombok.NonNull;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
public class CommentNotFoundException extends CommentException {

    private final Long commentId;

    public CommentNotFoundException(@NonNull String message, @NonNull Long commentId) {
        super(message, ErrorCode.BAD_REQUEST, HttpStatus.NOT_FOUND);
        this.commentId = commentId;
    }

    public CommentNotFoundException(@NonNull Long commentId) {
        this(String.format("존재하지 않는 댓글[commentId=%s]입니다.", commentId), commentId);
    }

}
