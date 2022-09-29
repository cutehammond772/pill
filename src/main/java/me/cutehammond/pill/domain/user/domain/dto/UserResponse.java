package me.cutehammond.pill.domain.user.domain.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.user.domain.User;
import me.cutehammond.pill.global.oauth.entity.Provider;
import me.cutehammond.pill.global.oauth.entity.Role;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserResponse {

    @NonNull
    private final String userId;

    @NonNull
    private final String userName;

    @NonNull
    private final String email;

    @NonNull
    private final String profileUrl;

    @NonNull
    private final Provider provider;

    @NonNull
    private final Role role;

    public static UserResponse getResponse(@NonNull User user) {
        return new UserResponse(user.getUserId(), user.getUserName(), user.getEmail(),
                user.getProfileUrl(), user.getProvider(), user.getRole());
    }

}
