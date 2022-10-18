package me.cutehammond.pill.global.exception.handler;

import me.cutehammond.pill.domain.point.domain.PillPointOrder;
import me.cutehammond.pill.domain.point.exception.PillPointOrderDuplicatedException;
import me.cutehammond.pill.domain.point.exception.particular.ParticularPillPointException;
import me.cutehammond.pill.domain.user.exception.PillUserException;
import me.cutehammond.pill.global.exception.APIErrorResponse;
import me.cutehammond.pill.global.exception.PillException;
import me.cutehammond.pill.global.oauth.entity.AuthToken;
import me.cutehammond.pill.global.oauth.exception.token.PillAuthTokenException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class PillExceptionHandler {

    @ExceptionHandler(PillPointOrderDuplicatedException.class)
    public ResponseEntity<APIErrorResponse> handlePillPointOrderDuplicatedException(PillPointOrderDuplicatedException e) {
        APIErrorResponse apiErrorResponse = defaultBuild(e)
                .extra(e.getDuplicate().stream().collect(
                        Collectors.toMap(PillPointOrder::name, o -> o.getAttr().name())
                        )
                )
                .build();

        return ResponseEntity.status(e.getHttpStatus()).body(apiErrorResponse);
    }

    @ExceptionHandler(ParticularPillPointException.class)
    public ResponseEntity<APIErrorResponse> handleParticularPillPointException(ParticularPillPointException e) {
        APIErrorResponse apiErrorResponse = defaultBuild(e)
                .extra(Map.of("pointName", e.getPointName()))
                .build();

        return ResponseEntity.status(e.getHttpStatus()).body(apiErrorResponse);
    }

    @ExceptionHandler(PillUserException.class)
    public ResponseEntity<APIErrorResponse> handlePillUserException(PillUserException e) {
        APIErrorResponse apiErrorResponse = defaultBuild(e)
                .extra(Map.of("userId", e.getUserId()))
                .build();

        return ResponseEntity.status(e.getHttpStatus()).body(apiErrorResponse);
    }

    @ExceptionHandler(PillAuthTokenException.class)
    public ResponseEntity<APIErrorResponse> handlePillAuthTokenException(PillAuthTokenException e) {
        APIErrorResponse apiErrorResponse = defaultBuild(e)
                .extra(Map.of(AuthToken.AuthTokenType.class.getSimpleName(), e.getAuthTokenType().name()))
                .build();

        return ResponseEntity.status(e.getHttpStatus()).body(apiErrorResponse);
    }

    @ExceptionHandler(PillException.class)
    public ResponseEntity<APIErrorResponse> handlePillException(PillException e) {
        APIErrorResponse apiErrorResponse = defaultBuild(e).build();

        return ResponseEntity.status(e.getHttpStatus()).body(apiErrorResponse);
    }

    private APIErrorResponse.APIErrorResponseBuilder defaultBuild(PillException e) {
        return APIErrorResponse.builder()
                .errorCode(e.getErrorCode())
                .httpStatus(e.getHttpStatus())
                .specificCode(e.getClass().getSimpleName())
                .message(e.getMessage());
    }

}
