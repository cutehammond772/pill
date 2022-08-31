package me.cutehammond.pill.global.oauth.service;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.user.domain.User;
import me.cutehammond.pill.domain.user.domain.dao.UserRepository;
import me.cutehammond.pill.global.oauth.domain.Provider;
import me.cutehammond.pill.global.oauth.domain.Role;
import me.cutehammond.pill.global.oauth.domain.UserPrincipal;
import me.cutehammond.pill.global.oauth.exception.OAuth2ProviderMissMatchException;
import me.cutehammond.pill.global.oauth.info.OAuth2UserInfo;
import me.cutehammond.pill.global.oauth.info.OAuth2UserInfoFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;

@Service
@RequiredArgsConstructor
public final class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        try {
            return this.process(userRequest, user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
        Provider provider = Provider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, user.getAttributes());

        User savedUser = userRepository.findByUserId(userInfo.getId());

        if (savedUser != null) {
            if (provider != savedUser.getProvider()) {
                throw new OAuth2ProviderMissMatchException("Account Provider mismatched. [Registered: " + savedUser.getProvider().name() + "] != [Accessed: " + provider.name() + "]");
            }

            updateUser(savedUser, userInfo);
        } else {
            savedUser = createUser(userInfo, provider);
        }

        return UserPrincipal.create(savedUser, user.getAttributes());
    }

    private User createUser(OAuth2UserInfo userInfo, Provider provider) {
        User user = User.builder()
                .userId(userInfo.getId())
                .userName(userInfo.getName())
                .email(userInfo.getEmail())
                .profileUrl(userInfo.getImageUrl())
                .provider(provider)
                .role(Role.DEFAULT_USER)
                .build();

        return userRepository.saveAndFlush(user);
    }

    private User updateUser(User user, OAuth2UserInfo userInfo) {
        if (userInfo.getName() != null && !user.getUserName().equals(userInfo.getName())) {
            user.setUserName(userInfo.getName());
        }

        if (userInfo.getImageUrl() != null && !user.getProfileUrl().equals(userInfo.getImageUrl())) {
            user.setProfileUrl(userInfo.getImageUrl());
        }

        return user;
    }
}
