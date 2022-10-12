package me.cutehammond.pill.global.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.cutehammond.pill.global.exception.APIErrorResponse;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Slf4j
@Component
public class PillForbiddenAccessHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.error("Forbidden access: {}", accessDeniedException.getMessage());
        APIErrorResponse apiErrorResponse = APIErrorResponse.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .errorCode(ErrorCode.SPRING_BAD_REQUEST)
                .specificCode(accessDeniedException.getClass().getSimpleName())
                .message(accessDeniedException.getMessage())
                .build();

        APIErrorResponse.sendJSONResponse(response, apiErrorResponse);
    }
}
