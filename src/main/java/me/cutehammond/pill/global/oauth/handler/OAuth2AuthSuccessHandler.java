package me.cutehammond.pill.global.oauth.handler;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.global.config.properties.AppProperties;
import me.cutehammond.pill.global.oauth.entity.Provider;
import me.cutehammond.pill.global.oauth.info.OAuth2UserInfo;
import me.cutehammond.pill.global.oauth.info.OAuth2UserInfoFactory;
import me.cutehammond.pill.global.oauth.repository.OAuth2AuthorizationRequestRepository;
import me.cutehammond.pill.global.oauth.token.AuthToken;
import me.cutehammond.pill.global.oauth.token.AuthTokenProvider;
import me.cutehammond.pill.global.oauth.token.JwtAuthentication;
import me.cutehammond.pill.global.utils.cookie.CookieUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Optional;

import static me.cutehammond.pill.global.oauth.repository.OAuth2AuthorizationRequestRepository.*;

/**
 * 이 Handler 는 인증이 성공한 후, <br><br>
 * 1. 이전 과정에서 SecurityContext 에 등록된 OAuth2AuthenticationToken 을 가져옵니다. <br>
 * 2. 해당 Authentication 을 기반으로 accessToken 을 생성합니다. <br>
 * 3. SecurityContext 에 JwtAuthentication 를 등록합니다. 왜냐하면 OAuth2AuthenticationToken 내의 정보는
 * 이미 User에 담겨진 후 영속화되었으므로 필요하지 않기 때문이며 (1),
 * <br> 이후 AccessToken 을 이용하여 인증을 수행하기 때문이기도 합니다. (2)<br>
 * 4. refreshToken 을 등록합니다. <br>
 * 5. accessToken 이 담긴 리다이렉트 링크를 프론트 단으로 보냅니다. <br>
 * */
@Component
@RequiredArgsConstructor
public class OAuth2AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthTokenProvider tokenProvider;
    private final AppProperties appProperties;
    private final OAuth2AuthorizationRequestRepository authorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // accessToken 을 생성한다.
        AuthToken accessToken = createAccessToken((OAuth2AuthenticationToken) authentication);

        // accessToken 이 담긴 redirectUri 를 return 한다.
        String uri = determineUri(request, accessToken);

        // 이미 로직이 실행된 경우 중복 응답을 방지하기 위해 return 한다.
        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + uri);
            return;
        }

        // JwtAuthentication 등록
        JwtAuthentication jwtAuthentication = JwtAuthentication
                .prepared(accessToken)
                .authenticated(accessToken.getClaims().getAudience());

        SecurityContextHolder.getContext().setAuthentication(jwtAuthentication);

        // RefreshToken 등록
        tokenProvider.updateRefreshToken(request, response, accessToken);

        // 인증 과정에 사용했던 쿠키들을 삭제한다.
        clearAuthenticationAttributes(request, response);

        // redirect 요청을 보낸다.
        getRedirectStrategy().sendRedirect(request, response, uri);
    }

    private AuthToken createAccessToken(OAuth2AuthenticationToken oAuth2Token) {
        Provider provider = Provider.valueOf(oAuth2Token.getAuthorizedClientRegistrationId().toUpperCase());

        OAuth2User user = oAuth2Token.getPrincipal();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, user.getAttributes());

        // accessToken 을 생성한다.
        Date now = new Date();
        AuthToken accessToken = tokenProvider.createAccessToken(
                userInfo.getId(), new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        return accessToken;
    }

    private String determineUri(HttpServletRequest request, AuthToken accessToken) {
        Optional<String> redirectUri = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        // 허용된 redirect uri 가 아닌 경우
        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new IllegalArgumentException("We've got an Unauthorized Redirect URI and can't proceed with the authentication.");
        }

        // cookie 내의 redirect uri 가 존재하지 않으면 등록된 authorizedRedirectUri 중 첫 번째 uri 를 대신 사용한다.
        String targetUrl = redirectUri.orElse(appProperties.getOauth2().getAuthorizedRedirectUris().get(0));

        // access token 이 담긴 redirectUri 가 반환된다. 이때 프론트 단에서 이를 받아 저장한다.
        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", accessToken.getToken())
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return appProperties.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .map(URI::create)
                .anyMatch(u -> u.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                        && u.getPort() == clientRedirectUri.getPort());
    }
}
