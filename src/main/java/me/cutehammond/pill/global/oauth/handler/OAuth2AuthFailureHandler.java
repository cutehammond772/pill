package me.cutehammond.pill.global.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.global.exception.APIErrorResponse;
import me.cutehammond.pill.global.exception.ErrorCode;
import me.cutehammond.pill.global.oauth.repository.OAuth2AuthorizationRequestRepository;
import me.cutehammond.pill.global.utils.PillSerializationUtils;
import me.cutehammond.pill.global.utils.cookie.dto.CookieResponse;
import me.cutehammond.pill.global.utils.cookie.CookieUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import static me.cutehammond.pill.global.oauth.repository.OAuth2AuthorizationRequestRepository.*;

// OAuth2AuthFailureHandler -> 'AbstractAuthenticationProcessingFilter'의 AuthenticationException 계열 Handler
// PillUnauthorizedAccessHandler -> 'ExceptionTranslationFilter'의 AuthenticationException 계열 Handler
/**
 * 'OAuth2 Login 과정에서 AuthenticationException 계열 예외가 발생할 때만' 이 Handler를 통해 처리합니다. <br>
 * 그 이외의 영역에서 발생한 AuthenticationException 계열 예외는 PillUnauthorizedAccessHandler에서 처리됩니다.
 */
@Component
@RequiredArgsConstructor
public class OAuth2AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final OAuth2AuthorizationRequestRepository authorizationRequestRepository;

    public static final String SPECIFIC_CODE = "OAuth2AuthenticationFailure";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException {
        // Login 실패 시 기존에 cookie 에 저장된 redirect uri 를 찾는다.
        String uri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(CookieResponse::getValue)
                .orElse("/");

        APIErrorResponse errorResponse = APIErrorResponse.builder()
                // InternalAuthenticationServiceException의 경우 서버 단에서의 예외이므로 Error를 달리 한다.
                .httpStatus((exception instanceof InternalAuthenticationServiceException) ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.UNAUTHORIZED)
                .errorCode((exception instanceof InternalAuthenticationServiceException) ? ErrorCode.SPRING_INTERNAL_ERROR : ErrorCode.SPRING_BAD_REQUEST)
                .specificCode(SPECIFIC_CODE)
                .message(exception.getMessage())
                .extra(Map.of(REDIRECT_URI_PARAM_COOKIE_NAME, uri))
                .build();

        // 'response' parameter에 APIErrorResponse를 실어서 보낸다.
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(errorResponse);

        // response에 APIErrorResponse를 실어서 보낸다.
        uri = UriComponentsBuilder
                .fromUriString(uri)
                .queryParam("response", Base64.getEncoder().encodeToString(json.getBytes()))
                .toUriString();

        // 기존 auth request 를 위해 저장된 cookie 들을 지운다.
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        getRedirectStrategy().sendRedirect(request, response, uri);
    }
}
