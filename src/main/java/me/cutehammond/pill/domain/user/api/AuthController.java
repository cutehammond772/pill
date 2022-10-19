package me.cutehammond.pill.domain.user.api;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.user.application.AuthService;
import me.cutehammond.pill.global.oauth.exception.token.AuthTokenNotFoundException;
import me.cutehammond.pill.global.oauth.entity.AuthToken;
import me.cutehammond.pill.global.utils.cookie.dto.CookieResponse;
import me.cutehammond.pill.global.utils.cookie.CookieUtils;
import me.cutehammond.pill.global.utils.HeaderUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static me.cutehammond.pill.global.oauth.auth.AuthTokenProvider.REFRESH_TOKEN;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/access")
    public ResponseEntity<String> getAccessToken(HttpServletRequest request, HttpServletResponse response) {
        /* Cookie로부터 RefreshToken 가져오기 */
        CookieResponse tokenCookie = CookieUtils.getCookie(request, REFRESH_TOKEN)
                .orElseThrow(() -> new AuthTokenNotFoundException(AuthToken.AuthTokenType.REFRESH_TOKEN));

        return ResponseEntity.ok(
                authService.issueAccessToken(request, response, tokenCookie.getValue()).getToken()
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> updateRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        /* Header 로부터 AccessToken 가져오기 */
        String accessTokenStr = HeaderUtils.getAccessToken(request)
                .orElseThrow(() -> new AuthTokenNotFoundException(AuthToken.AuthTokenType.ACCESS_TOKEN));

        authService.updateRefreshToken(request, response, accessTokenStr);
        return ResponseEntity.ok().build();
    }

}

