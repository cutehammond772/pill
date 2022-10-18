package me.cutehammond.pill.domain.user.domain.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import me.cutehammond.pill.global.oauth.entity.Provider;
import me.cutehammond.pill.global.oauth.entity.Role;

@Getter
@Builder
public final class UserRegisterRequest {

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

}
