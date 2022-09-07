package me.cutehammond.pill.domain.user.application;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.user.domain.AuthReqModel;
import me.cutehammond.pill.domain.user.domain.UserRefreshToken;
import me.cutehammond.pill.domain.user.domain.dao.sql.UserRefreshTokenRepository;
import me.cutehammond.pill.global.config.properties.AppProperties;
import me.cutehammond.pill.global.oauth.entity.UserPrincipal;
import me.cutehammond.pill.global.oauth.token.AuthToken;
import me.cutehammond.pill.global.oauth.token.AuthTokenProvider;
import me.cutehammond.pill.global.utils.CookieUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRefreshTokenRepository userRefreshTokenRepository;

    public static final long THREE_DAYS_MSEC = 259200000;
    public static final String REFRESH_TOKEN = "refresh_token";

    @Transactional
    public AuthToken authenticate(AuthReqModel model, Date now) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(model.getId(), model.getPassword())
        );

        String userId = model.getId();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AuthToken accessToken = tokenProvider.createAuthToken(
                userId,
                ((UserPrincipal) authentication.getPrincipal()).getRole(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        return accessToken;
    }

    @Transactional(readOnly = true)
    public boolean refreshTokenExists(String userId) {
        return userRefreshTokenRepository.findByUserId(userId) != null;
    }

    @Transactional
    public void updateRefreshToken(String userId, Date now, HttpServletRequest req, HttpServletResponse res) {
        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

        AuthToken refreshToken = tokenProvider.createAuthToken(
                appProperties.getAuth().getTokenSecret(),
                new Date(now.getTime() + refreshTokenExpiry)
        );

        // userId refresh token 으로 DB 확인
        UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUserId(userId);
        if (userRefreshToken == null) {
            // 없는 경우 새로 등록
            userRefreshToken = new UserRefreshToken(userId, refreshToken.getToken());
            userRefreshTokenRepository.save(userRefreshToken);
        } else {
            // DB에 refresh 토큰 업데이트
            userRefreshToken.setRefreshToken(refreshToken.getToken());
        }

        int cookieMaxAge = (int) refreshTokenExpiry / 60;
        CookieUtil.deleteCookie(req, res, REFRESH_TOKEN);
        CookieUtil.addCookie(res, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);
    }

}
