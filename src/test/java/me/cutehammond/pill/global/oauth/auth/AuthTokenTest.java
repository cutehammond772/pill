package me.cutehammond.pill.global.oauth.auth;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import me.cutehammond.pill.global.config.properties.AppProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
class AuthTokenTest {

    @Autowired
    private AuthTokenProvider authTokenProvider;

    @Autowired
    private AppProperties appProperties;

    @Test
    void token_validation_fail() {
        /* then */
        assertThatNullPointerException().isThrownBy(() -> {
            /* when */
            AuthToken token = authTokenProvider.createAccessToken(null, null);
        });
    }

    @Test
    void create_auth_token() {
        /* given */
        Date now = new Date();
        String userId = "cutehammond772";
        long expiration = now.getTime() + appProperties.getAuth().getRefreshTokenExpiry();

        /* when */
        AuthToken accessToken = authTokenProvider.createAccessToken(userId, new Date(expiration));
        AuthToken refreshToken = authTokenProvider.createRefreshToken(userId, new Date(expiration));

        Claims accessTokenClaims = accessToken.getClaims();
        Claims refreshTokenClaims = refreshToken.getClaims();

        /* then */
        // iss
        assertThat(accessTokenClaims.getIssuer()).isEqualTo(AuthToken.AUTH_TOKEN_ISSUER);
        assertThat(refreshTokenClaims.getIssuer()).isEqualTo(AuthToken.AUTH_TOKEN_ISSUER);

        // sub
        assertThat(accessTokenClaims.getSubject()).isEqualTo(AuthToken.AuthTokenType.ACCESS_TOKEN.name());
        assertThat(refreshTokenClaims.getSubject()).isEqualTo(AuthToken.AuthTokenType.REFRESH_TOKEN.name());

        // aud
        assertThat(accessTokenClaims.getAudience()).isEqualTo(userId);
        assertThat(refreshTokenClaims.getAudience()).isEqualTo(userId);

        // exp
        // - JWT expiration 설정 시 초단위 밑은 버리는 특성을 가진다.
        // - DefaultClaims.setExpiration()에서 setDateAsSeconds()를 통해 밀리초 단위는 버리는 것을 알 수 있다.
        assertThat(accessTokenClaims.getExpiration()).isEqualTo(new Date(expiration - expiration % 1000));
        assertThat(refreshTokenClaims.getExpiration()).isEqualTo(new Date(expiration - expiration % 1000));
    }

}