package me.cutehammond.pill.domain.user.api;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.user.application.UserService;
import me.cutehammond.pill.domain.user.domain.dto.UserResponse;
import me.cutehammond.pill.global.common.ApiResponse;
import me.cutehammond.pill.global.common.ApiResponseType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity getUser() {
        return ApiResponse.success(Map.of("user", userService.getCurrentUser().orElse(null)));
    }

    @GetMapping("/profile")
    public ResponseEntity getProfile() {
        UserResponse userResponse = userService.getCurrentUser().orElse(null);

        // api resource 는 인증된 사용자만 접근할 수 있도록 필터링되었으므로, 이 코드는 의미가 없다.
        if (userResponse == null)
            return ApiResponse.getResponse(ApiResponseType.UNAUTHORIZED);

        return ApiResponse.success(Map.of(
                "userId", userResponse.getUserId(),
                "profileUrl", userResponse.getProfileUrl(),
                "userName", userResponse.getUserName()
        ));
    }

}
