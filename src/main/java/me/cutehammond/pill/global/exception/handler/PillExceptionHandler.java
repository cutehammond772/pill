package me.cutehammond.pill.global.exception.handler;

import me.cutehammond.pill.global.exception.APIErrorResponse;
import me.cutehammond.pill.global.exception.PillException;
import me.cutehammond.pill.global.oauth.entity.AuthToken;
import me.cutehammond.pill.global.oauth.exception.token.PillAuthTokenException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class PillExceptionHandler {

    @ExceptionHandler(PillAuthTokenException.class)
    public ResponseEntity<APIErrorResponse> handlePillAuthTokenException(PillAuthTokenException e) {
        APIErrorResponse apiErrorResponse = APIErrorResponse.builder()
                .errorCode(e.getErrorCode())
                .httpStatus(e.getHttpStatus())
                .specificCode(e.getClass().getSimpleName())
                .message(e.getMessage())
                .extra(Map.of(AuthToken.AuthTokenType.class.getSimpleName(), e.getAuthTokenType().name()))
                .build();

        return ResponseEntity.status(e.getHttpStatus()).body(apiErrorResponse);
    }

    @ExceptionHandler(PillException.class)
    public ResponseEntity<APIErrorResponse> handlePillException(PillException e) {
        APIErrorResponse apiErrorResponse = APIErrorResponse.builder()
                .errorCode(e.getErrorCode())
                .httpStatus(e.getHttpStatus())
                .specificCode(e.getClass().getSimpleName())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(e.getHttpStatus()).body(apiErrorResponse);
    }

}
