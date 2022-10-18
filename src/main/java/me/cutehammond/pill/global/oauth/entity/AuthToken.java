package me.cutehammond.pill.global.oauth.entity;

import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.cutehammond.pill.global.oauth.exception.token.PillInvalidAuthTokenException;

import java.security.Key;
import java.util.Date;
import java.util.Objects;

/**
 * 인증 토큰 정보를 담은 객체입니다. 생성자 호출 시에 Validation 이 진행됩니다.
 * */
@Slf4j
@Getter
public final class AuthToken {

    /*
    Registered Claims:  iss(= Token issuer; Pill),
                        sub(= Token Name -> 'AccessToken' / 'RefreshToken'),
                        aud(= Audience; UserId),
                        exp(= Expiration),
                        iat(= Issued at)
     */

    public enum AuthTokenType {
        ACCESS_TOKEN, REFRESH_TOKEN,

        /** parsing 과정에서 예외가 발생할 때 사용한다. 왜냐하면 AuthTokenType은 Token 내에 존재하기 때문이다. */
        UNKNOWN
    }

    public static final String AUTH_TOKEN_ISSUER = "Pill";

    @NonNull
    private final String token;

    @NonNull
    private final Key key;

    @NonNull
    private final Claims claims;

    public AuthToken(@NonNull String token, @NonNull Key key) throws PillInvalidAuthTokenException {
        this.key = key;
        this.token = token;
        this.claims = extract();
    }

    public AuthToken(@NonNull String userId, @NonNull Date expiry, @NonNull Key key, @NonNull AuthTokenType type) throws PillInvalidAuthTokenException {
        this.key = key;
        this.token = create(userId, expiry, type);
        this.claims = extract();
    }

    private String create(String userId, Date expiry, AuthTokenType tokenType) {
        return Jwts.builder()
                // iss - Token Issuer
                .setIssuer(AUTH_TOKEN_ISSUER)
                // sub - Token Name
                .setSubject(tokenType.name())
                // aud - Audience (= UserId)
                .setAudience(userId)
                // exp - Expiration
                .setExpiration(expiry)
                // iat - issued at
                .setIssuedAt(new Date())

                // Hashing Algorithm
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extract() throws PillInvalidAuthTokenException {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SecurityException | MalformedJwtException | IllegalArgumentException e) {
            log.info("Invalid JWT signature.");
            throw new PillInvalidAuthTokenException("Failed to extract AuthToken; invalid JWT signature.", AuthTokenType.UNKNOWN);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            throw new PillInvalidAuthTokenException("Failed to extract AuthToken; expired JWT token.", AuthTokenType.UNKNOWN);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            throw new PillInvalidAuthTokenException("Failed to extract AuthToken; unsupported JWT token.", AuthTokenType.UNKNOWN);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().equals(AuthToken.class))
            return false;

        AuthToken authToken = (AuthToken) obj;
        return this.key.equals(authToken.key) && this.token.equals(authToken.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.token, this.key);
    }
}
