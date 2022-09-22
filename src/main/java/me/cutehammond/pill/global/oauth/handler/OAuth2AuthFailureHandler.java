package me.cutehammond.pill.global.oauth.handler;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.global.oauth.repository.OAuth2AuthorizationRequestRepository;
import me.cutehammond.pill.global.utils.cookie.CookieUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static me.cutehammond.pill.global.oauth.repository.OAuth2AuthorizationRequestRepository.*;

@Component
@RequiredArgsConstructor
public class OAuth2AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final OAuth2AuthorizationRequestRepository authorizationRequestRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        // 로그인 실패 시 기존에 cookie 에 저장된 redirect uri 를 찾는다.
        String url = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse("/");

        // 오류를 출력한다.
        exception.printStackTrace();

        // {redirect}?error=... 와 같이 redirect uri param 에 error 를 추가한다.
        url = UriComponentsBuilder.fromUriString(url)
                .queryParam("error", exception.getLocalizedMessage())
                .build().toUriString();

        // 기존 auth request 를 위해 저장된 cookie 들을 지운다.
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        // 위의 uri 로 redirect 를 요청한다.
        getRedirectStrategy().sendRedirect(request, response, url);
    }
}
