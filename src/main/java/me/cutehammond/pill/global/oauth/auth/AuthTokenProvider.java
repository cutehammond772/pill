package me.cutehammond.pill.global.oauth.auth;

import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.cutehammond.pill.global.config.properties.AppProperties;
import me.cutehammond.pill.global.oauth.entity.AuthToken;
import me.cutehammond.pill.global.oauth.exception.token.InvalidAuthTokenException;
import me.cutehammond.pill.global.utils.cookie.dto.CookieRequest;
import me.cutehammond.pill.global.utils.cookie.CookieSecureType;
import me.cutehammond.pill.global.utils.cookie.CookieUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class AuthTokenProvider {

    private final Key key;
    private final AppProperties properties;
    public static final String REFRESH_TOKEN = "refresh_token";

    protected AuthTokenProvider(
            @Value("${jwt.secret}") String secret, AppProperties properties) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.properties = properties;
    }

    /**
     * AccessToken 특성의 AuthToken 을 생성합니다.
     * */
    public AuthToken createAccessToken(@NonNull String userId, @NonNull Date expiry) throws InvalidAuthTokenException {
        return new AuthToken(userId, expiry, key, AuthToken.AuthTokenType.ACCESS_TOKEN);
    }

    /**
     * RefreshToken 특성의 AuthToken 을 생성합니다.
     * */
    public AuthToken createRefreshToken(@NonNull String userId, @NonNull Date expiry) throws InvalidAuthTokenException {
        return new AuthToken(userId, expiry, key, AuthToken.AuthTokenType.REFRESH_TOKEN);
    }

    /**
     * Token 문자열을 AuthToken 으로 변환합니다.
     * */
    public AuthToken convertAuthToken(@NonNull String token) throws InvalidAuthTokenException {
        return new AuthToken(token, key);
    }

    /** 사용자가 가지고 있는 RefreshToken 을 이용하여 새로운 AccessToken 을 발급합니다. <br>
     * @throws InvalidAuthTokenException */
    public AuthToken issueAccessToken(@NonNull AuthToken refreshToken) throws InvalidAuthTokenException {
        // 이 토큰이 RefreshToken 인지 따진다.
        if (!AuthToken.AuthTokenType.REFRESH_TOKEN.name().equals(refreshToken.getClaims().getSubject()))
            throw new InvalidAuthTokenException("Cannot issue AccessToken; received RefreshToken is not RefreshToken.", AuthToken.AuthTokenType.REFRESH_TOKEN);

        // userId 의 유효 여부는 JwtAuthenticationProvider 에서 이미 검증하였으므로 여기서는 검증 로직이 불필요하다.
        String userId = refreshToken.getClaims().getAudience();
        long tokenExpiry = properties.getAuth().getTokenExpiry();
        Date now = new Date();

        return createAccessToken(userId, new Date(now.getTime() + tokenExpiry));
    }

    /** auth 과정 이후 로그인을 유지하기 위해 RefreshToken 을 발급한 후 등록합니다.<br>
     * 만약 기존에 RefreshToken 이 존재할 경우 삭제 후 새로 등록합니다.
     * @throws InvalidAuthTokenException */
    public void updateRefreshToken(HttpServletRequest req, HttpServletResponse res, @NonNull AuthToken accessToken) throws InvalidAuthTokenException {
        // 이 토큰이 RefreshToken 인지 따진다.
        if (!AuthToken.AuthTokenType.ACCESS_TOKEN.name().equals(accessToken.getClaims().getSubject()))
            throw new InvalidAuthTokenException("Cannot update RefreshToken; received AccessToken is not AccessToken.", AuthToken.AuthTokenType.ACCESS_TOKEN);

        String userId = accessToken.getClaims().getAudience();
        long refreshTokenExpiry = properties.getAuth().getRefreshTokenExpiry();
        Date now = new Date();

        AuthToken refreshToken = createRefreshToken(userId, new Date(now.getTime() + refreshTokenExpiry));

        // 기존의 RefreshToken (Cookie)를 삭제한다.
        CookieUtils.deleteCookie(req, res, REFRESH_TOKEN);

        int cookieMaxAge = (int) refreshTokenExpiry / 60;
        CookieRequest cookieRequest = CookieRequest.builder()
                .name(REFRESH_TOKEN)
                .value(refreshToken.getToken())
                .maxAge(cookieMaxAge)
                // STRICT 설정을 함으로써 서버 단에서만 접근할 수 있도록 한다.
                .secureType(CookieSecureType.STRICT)
                .build();

        // 새로운 RefreshToken (Cookie)를 추가한다.
        CookieUtils.addCookie(res, cookieRequest);
    }

}
