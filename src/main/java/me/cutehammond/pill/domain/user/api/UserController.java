package me.cutehammond.pill.domain.user.api;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.user.application.UserService;
import me.cutehammond.pill.domain.user.domain.dto.response.UserProfileResponse;
import me.cutehammond.pill.domain.user.domain.dto.response.UserResponse;
import me.cutehammond.pill.domain.user.exception.PillUserNotFoundException;
import me.cutehammond.pill.domain.user.exception.PillUserUnauthorizedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(value = {"/", "/{userId}"})
    public ResponseEntity<UserResponse> getUser(@PathVariable(required = false) Optional<String> userId) {
        return ResponseEntity.ok(getUserResponse(userId));
    }

    @GetMapping(value = {"/profile/{userId}", "/profile"})
    public ResponseEntity<?> getProfile(@PathVariable(required = false) Optional<String> userId) {
        return ResponseEntity.ok(UserProfileResponse.from(getUserResponse(userId)));
    }

    private UserResponse getUserResponse(Optional<String> userId) {
        return userId.isPresent() ?
                userService.getUser(userId.get())
                        .orElseThrow(() -> new PillUserNotFoundException(userId.get())) :
                userService.getCurrentUser()
                        .orElseThrow(PillUserUnauthorizedException::new);
    }

}
