package me.cutehammond.pill.global.oauth.repository;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import me.cutehammond.pill.global.utils.cookie.CookieRequest;
import me.cutehammond.pill.global.utils.cookie.CookieSecureType;
import me.cutehammond.pill.global.utils.cookie.CookieUtil;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
    private static final int cookieExpireSeconds = 180;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return CookieUtil.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(request, response);
            return;
        }

        CookieRequest cookieRequest = CookieRequest.builder()
                .name(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .value(CookieUtil.serialize(authorizationRequest))
                .maxAge(cookieExpireSeconds)
                .secureType(CookieSecureType.STRICT)
                .build();

        CookieUtil.addCookie(response, cookieRequest);
        String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);

        if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
            CookieRequest redirectCookieRequest = CookieRequest.builder()
                    .name(REDIRECT_URI_PARAM_COOKIE_NAME)
                    .value(redirectUriAfterLogin)
                    .maxAge(cookieExpireSeconds)
                    .secureType(CookieSecureType.STRICT)
                    .build();

            CookieUtil.addCookie(response, redirectCookieRequest);
        }
    }

    @Override
    @Deprecated
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        return this.loadAuthorizationRequest(request);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return this.loadAuthorizationRequest(request);
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        CookieUtil.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
    }

}
