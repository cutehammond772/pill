package me.cutehammond.pill.domain.user.application;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.user.domain.dao.sql.UserRepository;
import me.cutehammond.pill.domain.user.domain.dto.UserResponse;
import me.cutehammond.pill.global.oauth.auth.JwtAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<UserResponse> getUser(String userId) {
        return userRepository.findByUserId(userId).map(UserResponse::getResponse);
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

}
