package me.cutehammond.pill.global.exception.handler;

import me.cutehammond.pill.domain.category.exception.CategoryException;
import me.cutehammond.pill.domain.category.exception.CategoryInvalidPageRequestException;
import me.cutehammond.pill.domain.category.exception.InvalidCategoryException;
import me.cutehammond.pill.domain.comment.exception.CommentInvalidPageRequestException;
import me.cutehammond.pill.domain.comment.exception.CommentNotFoundException;
import me.cutehammond.pill.domain.comment.exception.InvalidCommentException;
import me.cutehammond.pill.domain.point.domain.PillPointOrder;
import me.cutehammond.pill.domain.point.exception.PillPointOrderDuplicatedException;
import me.cutehammond.pill.domain.point.exception.particular.ParticularPillPointException;
import me.cutehammond.pill.domain.user.exception.UserException;
import me.cutehammond.pill.global.exception.APIErrorResponse;
import me.cutehammond.pill.global.exception.GeneralException;
import me.cutehammond.pill.global.oauth.entity.AuthToken;
import me.cutehammond.pill.global.oauth.exception.token.AuthTokenException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler(InvalidCommentException.class)
    public ResponseEntity<APIErrorResponse> handleInvalidCommentException(InvalidCommentException e) {
        Map<String, String> values = new HashMap<>();

        if (e.getCommentId() != -1L)
            values.put("commentId", Long.toString(e.getCommentId()));

        if (e.getComment() != null)
            values.put("comment", e.getComment());

        return build(common(e).extra(values));
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<APIErrorResponse> handleCommentNotFoundException(CommentNotFoundException e) {
        return build(common(e).extra(Map.of("commentId", Long.toString(e.getCommentId()))));
    }

    @ExceptionHandler(CommentInvalidPageRequestException.class)
    public ResponseEntity<APIErrorResponse> handleCommentInvalidPageRequestException(CommentInvalidPageRequestException e) {
        Map<String, String> values = new HashMap<>();

        values.put("page", Integer.toString(e.getPage()));
        values.put("size", Integer.toString(e.getSize()));

        return build(common(e).extra(values));
    }

    @ExceptionHandler(CategoryInvalidPageRequestException.class)
    public ResponseEntity<APIErrorResponse> handleCategoryPageOutOfIndexException(CategoryInvalidPageRequestException e) {
        Map<String, String> values = new HashMap<>();

        if (e.getCategoryId() != -1L)
            values.put("categoryId", Long.toString(e.getCategoryId()));

        if (e.getCategoryName() != null)
            values.put("categoryName", e.getCategoryName());

        values.put("page", Integer.toString(e.getPage()));
        values.put("size", Integer.toString(e.getSize()));

        return build(common(e).extra(values));
    }

    @ExceptionHandler(CategoryException.class)
    public ResponseEntity<APIErrorResponse> handleCategoryException(CategoryException e) {
        Map<String, String> values = new HashMap<>();

        if (e.getCategoryId() != -1L)
            values.put("categoryId", Long.toString(e.getCategoryId()));

        if (e.getCategoryName() != null)
            values.put("categoryName", e.getCategoryName());

        return build(common(e).extra(values));
    }

    @ExceptionHandler(PillPointOrderDuplicatedException.class)
    public ResponseEntity<APIErrorResponse> handlePillPointOrderDuplicatedException(PillPointOrderDuplicatedException e) {
        return build(common(e)
                .extra(
                        e.getDuplicate().stream().collect(
                                Collectors.toMap(PillPointOrder::name, o -> o.getAttr().name())
                        )
                ));
    }

    @ExceptionHandler(ParticularPillPointException.class)
    public ResponseEntity<APIErrorResponse> handleParticularPillPointException(ParticularPillPointException e) {
        return build(common(e)
                        .extra(Map.of("pointName", e.getPointName())
                    )
                );
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<APIErrorResponse> handlePillUserException(UserException e) {
        return build(common(e)
                .extra(Map.of("userId", e.getUserId())
                )
        );
    }

    @ExceptionHandler(AuthTokenException.class)
    public ResponseEntity<APIErrorResponse> handlePillAuthTokenException(AuthTokenException e) {
        return build(common(e)
                .extra(Map.of(AuthToken.AuthTokenType.class.getSimpleName(), e.getAuthTokenType().name())
                )
        );
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<APIErrorResponse> handleGeneralException(GeneralException e) {
        return build(common(e));
    }

    private ResponseEntity<APIErrorResponse> build(APIErrorResponse.APIErrorResponseBuilder builder) {
        APIErrorResponse apiErrorResponse = builder.build();
        return ResponseEntity.status(apiErrorResponse.getHttpStatus()).body(apiErrorResponse);
    }

    private APIErrorResponse.APIErrorResponseBuilder common(GeneralException e) {
        return APIErrorResponse.builder()
                .errorCode(e.getErrorCode())
                .httpStatus(e.getHttpStatus())
                .specificCode(e.getClass().getSimpleName())
                .message(e.getMessage());
    }

}
