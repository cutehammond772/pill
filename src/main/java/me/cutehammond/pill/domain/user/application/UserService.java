package me.cutehammond.pill.domain.user.application;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.user.domain.User;
import me.cutehammond.pill.domain.user.domain.dao.sql.UserRepository;
import me.cutehammond.pill.domain.user.domain.dto.UserResponse;
import me.cutehammond.pill.global.oauth.token.JwtAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getUser(String userId) {
        return UserResponse.getResponse(userRepository.findByUserId(userId));
    }

    /**
     * 현재 인증 상태인 사용자를 반환한다. <br> 이때 /auth/.. 경로로 요청이 들어온 경우 인증 정보가 없으므로 항상 null이 반환됨에 유의한다.
     * */
    @Transactional(readOnly = true)
    public Optional<UserResponse> getCurrentUser() {
        JwtAuthentication authentication = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null)
            return Optional.empty();

        return Optional.ofNullable(getUser(authentication.getPrincipal()));
    }

}
