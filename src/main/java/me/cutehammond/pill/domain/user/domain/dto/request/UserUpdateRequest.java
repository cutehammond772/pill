package me.cutehammond.pill.domain.user.domain.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import me.cutehammond.pill.global.oauth.entity.Role;

@Getter
@Builder
public class UserUpdateRequest {

    @NonNull
    private final String userId;

    private String profileUrl;

    private String userName;

    private Role role;

}
