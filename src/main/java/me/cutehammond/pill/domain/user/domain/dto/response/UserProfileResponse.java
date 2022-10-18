package me.cutehammond.pill.domain.user.domain.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UserProfileResponse {

    @NonNull
    private String profileUrl;

    @NonNull
    private String userName;

    @NonNull
    private String email;

    public static UserProfileResponse from(@NonNull UserResponse userResponse) {
        return new UserProfileResponse(userResponse.getProfileUrl(), userResponse.getUserName(), userResponse.getEmail());
    }

}
