package me.cutehammond.pill.domain.comment.exception;

import lombok.Getter;
import lombok.NonNull;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidCommentException extends CommentException {

    private final Long commentId;

    private final String comment;

    public InvalidCommentException(@NonNull String message, Long commentId, String comment) {
        super(message, ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        this.commentId = commentId;
        this.comment = comment;
    }

    public InvalidCommentException(@NonNull Long commentId) {
        this(String.format("유효하지 않는 댓글[commentId=%d]입니다.", commentId), commentId, null);
    }

    public InvalidCommentException(@NonNull String comment) {
        this(String.format("유효하지 않는 댓글[comment=%s]입니다.", comment), -1L, comment);
    }

}
