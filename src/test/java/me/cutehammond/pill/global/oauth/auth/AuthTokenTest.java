package me.cutehammond.pill.global.oauth.auth;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import me.cutehammond.pill.global.config.properties.AppProperties;
import me.cutehammond.pill.global.oauth.entity.AuthToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthToken 단위 테스트")
class AuthTokenTest {

    private AuthTokenProvider authTokenProvider;

    @Mock
    private AppProperties appProperties;

    @BeforeEach
    void setUp() {
        /* 임의의 jwt-secret-key를 주입한다.
        * 이때 @InjectMocks를 사용하지 않는 이유는, 생성자 방식 DI이므로 Reflection을 통해 key 값을 주입할 수 없기 때문이다. */
        authTokenProvider = new AuthTokenProvider("and0c2VjcmV0a2V5X3NwcmluZ2Jvb3R0ZXN0X2N1dGVoYW1tb25kNzcy", appProperties);
    }

    @Test
    @DisplayName("테스트 - 토큰 생성자에 null값을 주입한 토큰의 값 검증")
    void testTokenValidationFail() {
        /* then */
        assertThatNullPointerException().isThrownBy(() -> {
            /* when */
            AuthToken token = authTokenProvider.createAccessToken(null, null);
        });
    }

    @Test
    @DisplayName("테스트 - 임의의 유효한 값을 주입하여 생성한 토큰의 유효성과 동일성 검증")
    void testCreatingAuthToken() {
        /* given */
        given(appProperties.getAuth()).willReturn(new AppProperties.Auth(null, 0L, 604800000L));
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