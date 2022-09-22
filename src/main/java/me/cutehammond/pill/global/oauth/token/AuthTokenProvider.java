package me.cutehammond.pill.global.oauth.token;

import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import me.cutehammond.pill.global.config.properties.AppProperties;
import me.cutehammond.pill.global.oauth.exception.AuthenticationNotFoundException;
import me.cutehammond.pill.global.utils.cookie.CookieRequest;
import me.cutehammond.pill.global.utils.cookie.CookieSecureType;
import me.cutehammond.pill.global.utils.cookie.CookieUtil;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.Date;

@Slf4j
public class AuthTokenProvider {

    private final Key key;
    private final AppProperties properties;
    public static final String REFRESH_TOKEN = "refresh_token";

    public AuthTokenProvider(String secret, AppProperties properties) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.properties = properties;
    }

    public AuthToken createAccessToken(String userId, Date expiry) {
        return new AuthToken(userId, expiry, key, AuthToken.AuthTokenType.ACCESS_TOKEN);
    }

    public AuthToken createRefreshToken(String userId, Date expiry) {
        return new AuthToken(userId, expiry, key, AuthToken.AuthTokenType.REFRESH_TOKEN);
    }

    public AuthToken convertAuthToken(String token) {
        return new AuthToken(token, key);
    }

    /** 프론트 단에 저장된 refreshToken 을 이용하여 새로운 accessToken 을 발급합니다.*/
    public AuthToken issueAccessToken(AuthToken refreshToken) {
        // 현재 백엔드 단에서 인증이 완료된 상태일 때, accessToken 을 생성할 수 있다.
        JwtAuthentication authentication = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null)
            throw new AuthenticationNotFoundException();

        // refreshToken 의 유효 여부를 따진다.
        if (!refreshToken.validate())
            throw new IllegalArgumentException("Refresh token is invalid.");

        // 이 토큰이 RefreshToken 인지 따진다.
        if (!AuthToken.AuthTokenType.REFRESH_TOKEN.name().equals(refreshToken.getTokenClaims().getSubject()))
            throw new IllegalArgumentException("This token is not refresh token.");

        // userId 의 유효 여부는 JwtAuthenticationProvider 에서 이미 검증하였으므로 여기서는 검증 로직이 불필요하다.
        String userId = authentication.getPrincipal();
        long refreshTokenExpiry = properties.getAuth().getRefreshTokenExpiry();
        Date now = new Date();

        AuthToken accessToken = createAccessToken(userId, new Date(now.getTime() + refreshTokenExpiry));

        return accessToken;
    }

    /** auth 과정 이후 로그인을 유지하기 위해 refreshToken 을 발급한 후 등록합니다. <br>
     * 만약 기존에 refreshToken 이 존재할 경우 삭제 후 새로 추가합니다. */
    public void issueRefreshToken(HttpServletRequest req, HttpServletResponse res) {
        // 현재 백엔드 단에서 인증이 완료된 상태일 때, accessToken 을 생성할 수 있다.
        JwtAuthentication authentication = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null)
            throw new AuthenticationNotFoundException();

        String userId = authentication.getPrincipal();
        long refreshTokenExpiry = properties.getAuth().getRefreshTokenExpiry();
        Date now = new Date();

        AuthToken refreshToken = createRefreshToken(userId, new Date(now.getTime() + refreshTokenExpiry));

        // 기존의 RefreshToken (Cookie)를 삭제한다.
        CookieUtil.deleteCookie(req, res, REFRESH_TOKEN);

        int cookieMaxAge = (int) refreshTokenExpiry / 60;
        CookieRequest cookieRequest = CookieRequest.builder()
                .name(REFRESH_TOKEN)
                .value(refreshToken.getToken())
                .maxAge(cookieMaxAge)
                // STRICT 설정을 함으로써 백엔드 단에서만 접근할 수 있도록 한다.
                .secureType(CookieSecureType.STRICT)
                .build();

        // 새로운 RefreshToken (Cookie)를 추가한다.
        CookieUtil.addCookie(res, cookieRequest);
    }

}
