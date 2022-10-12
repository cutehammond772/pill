package me.cutehammond.pill.global.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.cutehammond.pill.global.exception.APIErrorResponse;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * 미인증 접근 (HTTP 401)이 발생할 때 이 Handler에서 처리합니다. <br>
 * 즉, 로그인이 안 된 상태에서 (=사용자의 권한을 알 수 없는 상태에서) 권한이 필요한 접근이 발생할 때 이 Handler에서 처리합니다. <br><br>
 * AuthenticationException이 발생할 시
 */
@Slf4j
@Component
public class PillUnauthorizedAccessHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error("Unauthorized access: {}", authException.getMessage());
        APIErrorResponse apiErrorResponse = APIErrorResponse.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .errorCode(ErrorCode.SPRING_BAD_REQUEST)
                .specificCode(authException.getClass().getSimpleName())
                .message(authException.getMessage())
                .build();

        APIErrorResponse.sendJSONResponse(response, apiErrorResponse);
    }

}
