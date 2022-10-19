package me.cutehammond.pill.domain.user.application;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.global.oauth.auth.AuthTokenProvider;
import me.cutehammond.pill.global.oauth.entity.AuthToken;
import me.cutehammond.pill.global.oauth.exception.token.InvalidAuthTokenException;
import me.cutehammond.pill.global.utils.cookie.CookieUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static me.cutehammond.pill.global.oauth.auth.AuthTokenProvider.REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthTokenProvider authTokenProvider;

    /** 유효한 refreshToken 을 이용하여 access token 을 요청한다. 이때 다른 요청과는 다르게 authorization header 가 포함되지 않는다. */
    @Transactional(propagation = Propagation.SUPPORTS)
    public AuthToken issueAccessToken(HttpServletRequest request, HttpServletResponse response, String refreshTokenStr) {
        try {
            return authTokenProvider.issueAccessToken(
                    authTokenProvider.convertAuthToken(refreshTokenStr)
            );
        } catch (InvalidAuthTokenException e) {
            // 유효하지 않은 token은 삭제된다.
            CookieUtils.deleteCookie(request, response, REFRESH_TOKEN);
            throw e;
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void updateRefreshToken(HttpServletRequest request, HttpServletResponse response, String accessTokenStr) {
        AuthToken accessToken = authTokenProvider.convertAuthToken(accessTokenStr);
        authTokenProvider.updateRefreshToken(request, response, accessToken);
    }

}
