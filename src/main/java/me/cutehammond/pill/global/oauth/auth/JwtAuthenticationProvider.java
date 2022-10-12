package me.cutehammond.pill.global.oauth.auth;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.cutehammond.pill.domain.user.application.UserService;
import me.cutehammond.pill.global.oauth.entity.AuthToken;
import me.cutehammond.pill.global.oauth.exception.authentication.AuthenticationInvalidTokenException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    /*
    Registered Claims:  iss(= Token issuer; Pill),
                        sub(= Token Name -> 'AccessToken' / 'RefreshToken'),
                        aud(= Audience; UserId),
                        exp(= Expiration),
                        iat(= Issued at)
     */

    private final UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthentication jwtAuthentication = (JwtAuthentication) authentication;
        Claims claims = jwtAuthentication.getToken().getClaims();

        String issuer = claims.getIssuer();
        String userId = claims.getAudience();
        String tokenName = claims.getSubject();

        if (!AuthToken.AUTH_TOKEN_ISSUER.equals(issuer))
            throw new AuthenticationInvalidTokenException("An issuer of this token is invalid; it must be 'Pill'.");

        if (!AuthToken.AuthTokenType.ACCESS_TOKEN.name().equals(tokenName))
            throw new AuthenticationInvalidTokenException("A subject of this token must be 'AccessToken'.");

        var userOptional = userService.getUser(userId);

        if (userOptional.isEmpty())
            throw new AuthenticationInvalidTokenException("Cannot find user '" + userId + "'.");

        return jwtAuthentication.authenticated(userOptional.get());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthentication.class.isAssignableFrom(authentication);
    }
}
