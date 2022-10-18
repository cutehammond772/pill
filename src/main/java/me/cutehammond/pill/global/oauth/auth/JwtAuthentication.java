package me.cutehammond.pill.global.oauth.auth;

import lombok.Getter;
import lombok.NonNull;
import me.cutehammond.pill.domain.user.domain.dto.response.UserResponse;
import me.cutehammond.pill.global.oauth.entity.AuthToken;
import me.cutehammond.pill.global.oauth.exception.authentication.AuthenticationInvalidTokenException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

public final class JwtAuthentication implements Authentication {

    @NonNull
    @Getter
    private final AuthToken token;

    private final String userId;
    private Collection<? extends GrantedAuthority> authorities;

    private boolean authenticated = false;

    /** AuthenticationManager 에서 인증(Authenticate) 과정에 필요한 정보를 담습니다. */
    public static JwtAuthentication prepared(@NonNull AuthToken token) {
        return new JwtAuthentication(token);
    }

    /** 인증이 성공한 뒤 검증된 principal 이 담긴 객체를 생성합니다. */
    public JwtAuthentication authenticated(@NonNull UserResponse userResponse) {
        JwtAuthentication jwtAuthentication = new JwtAuthentication(token, userResponse.getUserId());

        if (!getToken().getClaims().getAudience().equals(userResponse.getUserId()))
            throw new AuthenticationInvalidTokenException();

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userResponse.getRole().getKey());

        jwtAuthentication.authorities = List.of(authority);
        jwtAuthentication.authenticated = true;
        return jwtAuthentication;
    }

    private JwtAuthentication(AuthToken token) {
        this(token, null);
    }

    private JwtAuthentication(AuthToken token, String userId) {
        this.token = token;
        this.userId = userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities == null ? List.of() : authorities;
    }

    /** JWT 의 경우 password 가 필요하지 않으므로 credentials 가 존재하지 않습니다. */
    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    @Deprecated
    public Object getDetails() {
        return null;
    }

    @Override
    public String getPrincipal() {
        return userId;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        throw new InternalAuthenticationServiceException("setAuthenticated(boolean) cannot access. Use prepared() or authenticated() instead.");
    }

    /** getPrincipal()과 동일한 값을 반환합니다. */
    @Override
    @Deprecated
    public String getName() {
        return userId;
    }
}
