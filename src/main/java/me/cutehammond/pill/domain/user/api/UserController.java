package me.cutehammond.pill.domain.user.api;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.user.application.UserService;
import me.cutehammond.pill.domain.user.domain.dto.UserResponse;
import me.cutehammond.pill.domain.user.exception.PillUserUnauthorizedException;
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
    public ResponseEntity<UserResponse> getUser() {
        var userOptional = userService.getCurrentUser();

        // api resource 는 인증된 사용자만 접근할 수 있도록 필터링되었으므로, 구색 맞추기용 코드이다..
        if (userOptional.isEmpty())
            throw new PillUserUnauthorizedException();

        return ResponseEntity.ok(userOptional.get());
    }

    @GetMapping("/profile")
    public ResponseEntity getProfile() {
        var userOptional = userService.getCurrentUser();

        // api resource 는 인증된 사용자만 접근할 수 있도록 필터링되었으므로, 구색 맞추기용 코드이다..
        if (userOptional.isEmpty())
            throw new PillUserUnauthorizedException();

        UserResponse userResponse = userOptional.get();

        return ResponseEntity.ok(
                Map.of(
                        "userId", userResponse.getUserId(),
                        "profileUrl", userResponse.getProfileUrl(),
                        "userName", userResponse.getUserName()
                )
        );
    }

}
