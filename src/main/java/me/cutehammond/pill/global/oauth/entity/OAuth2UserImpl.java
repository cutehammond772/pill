package me.cutehammond.pill.global.oauth.entity;

import lombok.*;
import me.cutehammond.pill.domain.user.domain.dto.response.UserResponse;
import me.cutehammond.pill.global.oauth.info.OAuth2UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * OAuth2 Login 시 Authentication 에 저장되는 사용자 정보 객체입니다. <br>
 * 이후 JwtAuthentication 로 교체되므로 비즈니스 로직에서는 사용되지 않습니다.
 * */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class OAuth2UserImpl implements OAuth2User {

    private final String userId;
    private final Provider provider;
    private final Role role;
    private final Collection<GrantedAuthority> authorities;

    @Setter
    private Map<String, Object> attributes;

    @Override
    public String getName() {
        return userId;
    }

    public static OAuth2UserImpl from(@NonNull UserResponse userResponse) {
        return new OAuth2UserImpl(userResponse.getUserId(), userResponse.getProvider(), userResponse.getRole(),
                List.of(new SimpleGrantedAuthority(userResponse.getRole().getKey())));
    }

    public static OAuth2UserImpl from(@NonNull UserResponse userResponse, @NonNull OAuth2UserInfo info) {
        OAuth2UserImpl oAuth2UserImpl = from(userResponse);
        oAuth2UserImpl.setAttributes(info.getAttributes());

        return oAuth2UserImpl;
    }

}
