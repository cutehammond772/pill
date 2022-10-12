package me.cutehammond.pill.global.exception.handler;

import me.cutehammond.pill.global.exception.APIErrorResponse;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

/**
 * spring 내부에서 발생하는 exception을 받아 ResponseEntity로 내보냅니다.
 */
@RestControllerAdvice
public class SpringInternalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        APIErrorResponse errorResponse = APIErrorResponse.builder()
                .httpStatus(status)
                .errorCode(ErrorCode.SPRING_INTERNAL_ERROR)
                .specificCode(ex.getClass().getSimpleName())
                .message(ex.getMessage())
                .build();

        return super.handleExceptionInternal(ex, errorResponse, headers, status, request);
    }
}
