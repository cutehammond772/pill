package me.cutehammond.pill.domain.user.api;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.global.common.ApiResponse;
import me.cutehammond.pill.global.common.ApiResponseType;
import me.cutehammond.pill.global.oauth.token.AuthToken;
import me.cutehammond.pill.global.oauth.token.AuthTokenProvider;
import me.cutehammond.pill.global.utils.cookie.CookieUtil;
import me.cutehammond.pill.global.utils.HeaderUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static me.cutehammond.pill.global.oauth.token.AuthTokenProvider.REFRESH_TOKEN;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthTokenProvider tokenProvider;

    /** 유효한 refreshToken 을 이용하여 access token 을 요청한다. 이때 다른 요청과는 다르게 authorization header 가 포함되지 않는다. */
    @GetMapping("/access")
    public ResponseEntity<String> getAccessToken(HttpServletRequest request, HttpServletResponse response) {
        // refreshToken 의 존재 여부를 확인한다.
        var tokenOptional = CookieUtil.getCookie(request, REFRESH_TOKEN);

        // 만약 존재하지 않을 경우 404 를 반환한다. (프론트 단에서 이를 받으면 로그인 창으로 유도한다.)
        if (tokenOptional.isEmpty())
            return ApiResponse.getResponse(ApiResponseType.NO_REFRESH_TOKEN);

        AuthToken refreshToken = tokenProvider.convertAuthToken(tokenOptional.map(Cookie::getValue).orElse(null));

        // refreshToken 의 유효 여부를 따진다.
        if (!refreshToken.validate()) {
            // 유효하지 않은 token 은 삭제한다.
            CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);

            return ApiResponse.getResponse(ApiResponseType.INVALID_ACCESS_TOKEN);
        }

        AuthToken accessToken = tokenProvider.issueAccessToken(refreshToken);
        return ApiResponse.success(accessToken.getToken());
    }

    /** 유효한 accessToken 을 이용하여 기존의 refreshToken 을 재발급합니다. <br>
     * 임의로 refreshToken 유효 기한을 연장할 때 요청됩니다. */
    @GetMapping("/refresh")
    public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // header 로부터 accessToken 가져오기
        AuthToken authToken = tokenProvider.convertAuthToken(HeaderUtil.getAccessToken(request));

        if (!authToken.validate())
            return ApiResponse.getResponse(ApiResponseType.INVALID_ACCESS_TOKEN);

        tokenProvider.issueRefreshToken(request, response);

        return ResponseEntity.ok().build();
    }
}

