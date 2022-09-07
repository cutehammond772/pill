package me.cutehammond.pill.global.oauth.service;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.user.domain.User;
import me.cutehammond.pill.domain.user.domain.dao.sql.UserRepository;
import me.cutehammond.pill.global.oauth.entity.Provider;
import me.cutehammond.pill.global.oauth.entity.Role;
import me.cutehammond.pill.global.oauth.entity.UserPrincipal;
import me.cutehammond.pill.global.oauth.exception.OAuth2ProviderMissMatchException;
import me.cutehammond.pill.global.oauth.info.OAuth2UserInfo;
import me.cutehammond.pill.global.oauth.info.OAuth2UserInfoFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
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

        // 이미 등록된 User
        User savedUser = userRepository.findByUserId(userInfo.getId());

        if (savedUser != null) {
            // userid 가 같지만 Provider 가 다를 경우 예외 처리 (한 userid 당 Provider 는 하나만 연동 가능하도록 한다.)
            if (provider != savedUser.getProvider()) {
                throw new OAuth2ProviderMissMatchException("Account Provider mismatched. [Registered: " + savedUser.getProvider().name() + "] != [Accessed: " + provider.name() + "]");
            }

            // Provider 가 제공하는 계정의 정보가 바뀌면 User 의 정보도 바뀌도록 한다.
            savedUser = updateUser(savedUser, userInfo);
        } else {
            savedUser = registerUser(userInfo, provider);
        }

        return UserPrincipal.create(savedUser, user.getAttributes());
    }

    private User registerUser(OAuth2UserInfo userInfo, Provider provider) {
        User user = User.builder()
                .userId(userInfo.getId())
                .userName(userInfo.getName()) // 처음에는 userid 와 username 이 같도록 설정한다.
                .email(userInfo.getEmail())
                .profileUrl(userInfo.getImageUrl())
                .provider(provider)
                .role(Role.DEFAULT_USER)
                .build();

        return userRepository.save(user);
    }

    private User updateUser(User user, OAuth2UserInfo userInfo) {
        if (userInfo.getName() != null && !user.getUserName().equals(userInfo.getName())) {
            user.setUserName(userInfo.getName());
        }

        if (userInfo.getImageUrl() != null && !user.getProfileUrl().equals(userInfo.getImageUrl())) {
            user.setProfileUrl(userInfo.getImageUrl());
        }

        return userRepository.save(user);
    }
}
