package me.cutehammond.pill.global.oauth.info;

import me.cutehammond.pill.global.oauth.entity.Provider;
import me.cutehammond.pill.global.oauth.info.impl.GoogleOAuth2UserInfo;

import java.util.Collections;
import java.util.Map;

public final class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(Provider provider, Map<String, Object> attributes) {
        // 백엔드 단에서 attributes 를 수정할 수 없도록 한다.
        attributes = Collections.unmodifiableMap(attributes);

        switch (provider) {
            case GOOGLE:
                return new GoogleOAuth2UserInfo(attributes);

            default:
                throw new IllegalArgumentException("Invalid Provider.");
        }
    }

}
