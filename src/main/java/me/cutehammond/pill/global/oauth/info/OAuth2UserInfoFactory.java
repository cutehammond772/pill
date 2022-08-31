package me.cutehammond.pill.global.oauth.info;

import me.cutehammond.pill.global.oauth.domain.Provider;
import me.cutehammond.pill.global.oauth.info.impl.GoogleOAuth2UserInfo;

import java.util.Map;

public final class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(Provider provider, Map<String, Object> attributes) {
        switch (provider) {
            case GOOGLE:
                return new GoogleOAuth2UserInfo(attributes);

            default:
                throw new IllegalArgumentException("Invalid Provider.");
        }
    }

}
