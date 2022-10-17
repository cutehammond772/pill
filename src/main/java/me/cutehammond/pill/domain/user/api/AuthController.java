package me.cutehammond.pill.domain.user.api;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.global.oauth.exception.token.PillAuthTokenNotFoundException;
import me.cutehammond.pill.global.oauth.entity.AuthToken;
import me.cutehammond.pill.global.oauth.auth.AuthTokenProvider;
import me.cutehammond.pill.global.utils.cookie.dto.CookieResponse;
import me.cutehammond.pill.global.utils.cookie.CookieUtils;
import me.cutehammond.pill.global.utils.HeaderUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static me.cutehammond.pill.global.oauth.auth.AuthTokenProvider.REFRESH_TOKEN;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthTokenProvider tokenProvider;

    /** 유효한 refreshToken 을 이용하여 access token 을 요청한다. 이때 다른 요청과는 다르게 authorization header 가 포함되지 않는다. */
    @GetMapping("/access")
    public ResponseEntity<String> getAccessToken(HttpServletRequest request, HttpServletResponse response) {
        // refreshToken 의 존재 여부를 확인한다.
        var tokenOptional = CookieUtils.getCookie(request, REFRESH_TOKEN);

        // 존재하지 않을 경우
        if (tokenOptional.isEmpty())
            throw new PillAuthTokenNotFoundException(AuthToken.AuthTokenType.REFRESH_TOKEN);

        try {
            AuthToken refreshToken = tokenProvider.convertAuthToken(tokenOptional.map(CookieResponse::getValue).orElse(null));
            AuthToken accessToken = tokenProvider.issueAccessToken(refreshToken);

            return ResponseEntity.ok(accessToken.getToken());
        } catch (Exception e) {
            // 유효하지 않은 token 은 삭제된다.
            CookieUtils.deleteCookie(request, response, REFRESH_TOKEN);

            throw e;
        }
    }

    /** 유효한 accessToken 을 이용하여 기존의 refreshToken 을 재발급합니다. <br>
     * 임의로 refreshToken 유효 기한을 연장할 때 요청됩니다. */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // header 로부터 accessToken 가져오기
        var accessToken = HeaderUtils.getAccessToken(request).map(tokenProvider::convertAuthToken);

        if (accessToken.isEmpty())
            throw new PillAuthTokenNotFoundException(AuthToken.AuthTokenType.ACCESS_TOKEN);

        tokenProvider.updateRefreshToken(request, response, accessToken.get());
        return ResponseEntity.ok().build();
    }
}

