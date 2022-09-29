package me.cutehammond.pill.global.oauth.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.cutehammond.pill.global.oauth.token.AuthToken;
import me.cutehammond.pill.global.oauth.token.AuthTokenProvider;
import me.cutehammond.pill.global.oauth.token.JwtAuthentication;
import me.cutehammond.pill.global.utils.HeaderUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final AntPathRequestMatcher AUTH_REQUEST_MATCHER = new AntPathRequestMatcher("/auth/**");

    private final AuthTokenProvider tokenProvider;
    private final HttpSecurity httpSecurity;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        /* auth request 의 경우 해당 필터를 넘기도록 한다.
         * ex. OAuth2 Login 과정 (-> 모두 /auth/..로 통일)
         * ex. 프론트 단으로부터 accessToken 을 가져오는 요청 (/auth/access)
         *
         * ps. Session Policy 가 STATELESS 이므로 매 요청마다 이 필터를 거치지 않으면 Authentication 은 null 임에 명심해야 한다.
         * 즉 /auth/.. 경로로 요청을 보내면 Authentication 은 항상 null 로 설정된다.
         */
        if (AUTH_REQUEST_MATCHER.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authentication 이 존재하지 않을 경우 등록한다.
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            // Header 내의 AccessToken 을 가져온다.
            String token = HeaderUtil.getAccessToken(request);

            if (token != null) {
                try {
                    // accessToken 이 존재하면 이를 AuthToken 으로 변환한다.
                    AuthToken authToken = tokenProvider.convertAuthToken(token);
                    AuthenticationManager authenticationManager = httpSecurity.getSharedObject(AuthenticationManager.class);
                    Authentication authentication = authenticationManager.authenticate(JwtAuthentication.prepared(authToken));

                    // authentication 등록 (= 인증)
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    e.printStackTrace();
                    // 어떤 오류라도 발생 시 인증 과정을 무효로 한다.
                    SecurityContextHolder.clearContext();
                }
            } else {
                // accessToken 이 존재하지 않는 경우 context 를 비운다.
                SecurityContextHolder.clearContext();
            }
        }

        // 다음 필터로 이동
        filterChain.doFilter(request, response);
    }

}
