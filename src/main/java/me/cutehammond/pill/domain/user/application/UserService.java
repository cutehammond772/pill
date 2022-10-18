package me.cutehammond.pill.domain.user.application;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.user.domain.User;
import me.cutehammond.pill.domain.user.domain.dao.sql.UserRepository;
import me.cutehammond.pill.domain.user.domain.dto.request.UserRegisterRequest;
import me.cutehammond.pill.domain.user.domain.dto.response.UserResponse;
import me.cutehammond.pill.domain.user.domain.dto.request.UserUpdateRequest;
import me.cutehammond.pill.domain.user.exception.PillUserAlreadyRegisteredException;
import me.cutehammond.pill.domain.user.exception.PillUserNotFoundException;
import me.cutehammond.pill.global.oauth.auth.JwtAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * userId를 가진 사용자를 반환합니다.
     */
    @Transactional(readOnly = true)
    public Optional<UserResponse> getUser(String userId) {
        return userRepository.findByUserId(userId).map(UserResponse::from);
    }

    /**
     * 현재 인증 상태인 사용자를 반환합니다. <br> 이때 /auth/.. 경로로 요청이 들어온 경우 인증 정보가 없으므로 항상 null이 반환됨에 유의합니다.
     * */
    @Transactional(readOnly = true)
    public Optional<UserResponse> getCurrentUser() {
        JwtAuthentication authentication = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null)
            return Optional.empty();

        return getUser(authentication.getPrincipal());
    }

    /**
     * UserRegisterRequest의 정보에 따라 사용자를 등록합니다.
     */
    @Transactional
    public UserResponse registerUser(UserRegisterRequest userRegisterRequest) {
        if (userRepository.findByUserId(userRegisterRequest.getUserId()).isPresent())
            throw new PillUserAlreadyRegisteredException(userRegisterRequest.getUserId());

        User user = new User(userRegisterRequest);
        return UserResponse.from(userRepository.save(user));
    }

    /**
     * UserUpdateRequest의 정보에 따라 기존 사용자의 정보를 변경합니다.
     */
    @Transactional
    public UserResponse updateUser(UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findByUserId(userUpdateRequest.getUserId())
                .orElseThrow(() -> new PillUserNotFoundException(userUpdateRequest.getUserId()));

        user.update(userUpdateRequest);
        return UserResponse.from(user);
    }

}
