package me.cutehammond.pill.global.oauth.service;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.user.application.UserService;
import me.cutehammond.pill.domain.user.domain.dto.request.UserRegisterRequest;
import me.cutehammond.pill.domain.user.domain.dto.response.UserResponse;
import me.cutehammond.pill.domain.user.domain.dto.request.UserUpdateRequest;
import me.cutehammond.pill.global.oauth.entity.Provider;
import me.cutehammond.pill.global.oauth.entity.Role;
import me.cutehammond.pill.global.oauth.entity.OAuth2UserImpl;
import me.cutehammond.pill.global.oauth.exception.authentication.AuthenticationProviderMissMatchException;
import me.cutehammond.pill.global.oauth.info.OAuth2UserInfo;
import me.cutehammond.pill.global.oauth.info.OAuth2UserInfoFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resource Server로부터 사용자(Resource Owner)의 정보를 Client로 가져온 후, 이 곳에서 사용자 등록을 진행합니다.
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    /**
     *
     * @param userRequest Resource Server로부터 가져온 사용자의 정보를 담고 있습니다.
     * @return 사용자 등록 후 가공된 OAuth2User를 반환합니다.
     */
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException, InternalAuthenticationServiceException {
        try {
            OAuth2User defaultUser = super.loadUser(userRequest);

            /* ClientRegistration은 등록된 OAuth2 Provider들을 담고 있다. registrationId는 그 Provider 의 lowercase name이다.  */
            Provider provider = Provider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

            /* provider 를 토대로 attributes 를 UserInfo 로 변환한다.  */
            OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, defaultUser.getAttributes());

            var userResponse = userService.getUser(userInfo.getId());
            return OAuth2UserImpl.from(userResponse.isEmpty() ?
                    register(userInfo, provider) : update(userInfo, provider, userResponse.get()), userInfo);
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    UserResponse register(OAuth2UserInfo userInfo, Provider provider) {
        /* User를 새로 등록하는 경우 */
        UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder()
                .userId(userInfo.getId())
                .userName(userInfo.getName())
                .email(userInfo.getEmail())
                .profileUrl(userInfo.getImageUrl())
                .provider(provider)
                .role(Role.DEFAULT_USER)
                .build();

        return userService.registerUser(userRegisterRequest);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    UserResponse update(OAuth2UserInfo userInfo, Provider provider, UserResponse userResponse) {
        /* 등록된 User가 존재하는 경우 */
        /* provider가 등록된 User와 다를 경우 예외 발생; 사용자는 하나의 provider만 연동 가능 */
        if (provider != userResponse.getProvider()) {
            throw new AuthenticationProviderMissMatchException
                    ("Account Provider mismatched. [Registered: " + userResponse.getProvider().name() + "] != [Accessed: " + provider.name() + "]");
        }

        var userUpdateRequest = UserUpdateRequest.builder()
                .userId(userResponse.getUserId());

        if (userInfo.getName() != null && !userResponse.getUserName().equals(userInfo.getName())) {
            userUpdateRequest.userName(userInfo.getName());
        }

        if (userInfo.getImageUrl() != null && !userResponse.getProfileUrl().equals(userInfo.getImageUrl())) {
            userUpdateRequest.profileUrl(userInfo.getImageUrl());
        }

        /* Role Update(관리자 <-> 일반 사용자 변경)는 별도 로직에서 처리한다. */
        return userService.updateUser(userUpdateRequest.build());
    }

}
