package me.cutehammond.pill.global.oauth.token;

import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.cutehammond.pill.global.oauth.entity.Role;

import java.security.Key;
import java.util.Date;

@Slf4j
@Getter
@RequiredArgsConstructor
public class AuthToken {

    /*
    Registered Claims:  iss(= Token issuer; Pill),
                        sub(= Token Name -> 'AccessToken' / 'RefreshToken'),
                        aud(= Audience; UserId),
                        exp(= Expiration),
                        iat(= Issued at)
     */

    public enum AuthTokenType {
        ACCESS_TOKEN, REFRESH_TOKEN;
    }

    public static final String AUTH_TOKEN_ISSUER = "Pill";

    @NonNull
    private final String token;

    @NonNull
    private final Key key;

    AuthToken(String userId, Date expiry, Key key, AuthTokenType type) {
        this.key = key;
        this.token = createAuthToken(userId, expiry, type);
    }

    private String createAuthToken(String userId, Date expiry, AuthTokenType tokenType) {
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

    public boolean validate() {
        return this.getTokenClaims() != null;
    }

    public Claims getTokenClaims() {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SecurityException e) {
            log.info("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token is invalid.");
        }

        return null;
    }

}
