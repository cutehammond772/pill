package me.cutehammond.pill.domain.user.api;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.user.application.AuthService;
import me.cutehammond.pill.domain.user.domain.AuthReqModel;
import me.cutehammond.pill.global.common.ApiResponse;
import me.cutehammond.pill.global.config.properties.AppProperties;
import me.cutehammond.pill.global.oauth.entity.Role;
import me.cutehammond.pill.global.oauth.token.AuthToken;
import me.cutehammond.pill.global.oauth.token.AuthTokenProvider;
import me.cutehammond.pill.global.utils.CookieUtil;
import me.cutehammond.pill.global.utils.HeaderUtil;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static me.cutehammond.pill.domain.user.application.AuthService.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<?> login(HttpServletRequest request, HttpServletResponse response, @RequestBody AuthReqModel authReqModel) {
        Date now = new Date();

        // Login (= Authenticate)
        AuthToken accessToken = authService.authenticate(authReqModel, now);

        // Updating Refresh Token
        authService.updateRefreshToken(authReqModel.getId(), now, request, response);

        return ApiResponse.success("token", accessToken.getToken());
    }

    @GetMapping("/refresh")
    public ApiResponse<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // access token 확인
        AuthToken authToken = tokenProvider.convertAuthToken(HeaderUtil.getAccessToken(request));
        if (!authToken.validate())
            return ApiResponse.invalidAccessToken();

        // expired access token 인지 확인
        Claims claims = authToken.getExpiredTokenClaims();
        if (claims == null) {
            return ApiResponse.notExpiredTokenYet();
        }

        String userId = claims.getSubject();
        Role role = Role.of(claims.get("role", String.class).toUpperCase());

        // refresh token
        String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
                .map(Cookie::getValue).get();

        AuthToken authRefreshToken = tokenProvider.convertAuthToken(refreshToken);

        if (!authRefreshToken.validate())
            return ApiResponse.invalidRefreshToken();

        // userId refresh token 으로 DB 확인
        if (!authService.refreshTokenExists(userId)) {
            return ApiResponse.invalidRefreshToken();
        }

        Date now = new Date();
        AuthToken accessToken = tokenProvider.createAuthToken(
                userId, role, new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        long validTime = authRefreshToken.getTokenClaims().getExpiration().getTime() - now.getTime();

        // refresh 토큰 기간이 3일 이하로 남은 경우, refresh 토큰 갱신
        if (validTime <= THREE_DAYS_MSEC)
            authService.updateRefreshToken(userId, now, request, response);

        return ApiResponse.success("token", accessToken.getToken());
    }
}

