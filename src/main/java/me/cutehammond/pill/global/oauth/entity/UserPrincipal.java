package me.cutehammond.pill.global.oauth.entity;

import lombok.*;
import me.cutehammond.pill.domain.user.domain.User;
import me.cutehammond.pill.global.oauth.info.OAuth2UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserPrincipal implements OAuth2User {

    private final String userId;
    private final Provider provider;
    private final Role role;
    private final Collection<GrantedAuthority> authorities;

    @Setter
    private Map<String, Object> attributes;

    /** OAuth2 Provider 에서 제공받은 각종 정보를 나타낸다. */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return userId;
    }

    public static UserPrincipal create(User user) {
        return new UserPrincipal(user.getUserId(), user.getProvider(), Role.DEFAULT_USER,
                List.of(new SimpleGrantedAuthority(Role.DEFAULT_USER.getKey())));
    }

    public static UserPrincipal create(User user, OAuth2UserInfo info) {
        UserPrincipal userPrincipal = create(user);
        userPrincipal.setAttributes(info.getAttributes());

        return userPrincipal;
    }

}
