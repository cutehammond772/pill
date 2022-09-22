package me.cutehammond.pill.domain.user.application;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.user.domain.User;
import me.cutehammond.pill.domain.user.domain.dao.sql.UserRepository;
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
    public User getUser(String userId) {
        return userRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<User> getCurrentUser() {
        JwtAuthentication authentication = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null)
            return Optional.empty();

        return Optional.ofNullable(getUser(authentication.getPrincipal()));
    }

}
