package me.cutehammond.pill.global.config.security;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.global.config.properties.CorsProperties;
import me.cutehammond.pill.global.oauth.entity.Role;
import me.cutehammond.pill.global.oauth.handler.exception.RestAuthenticationEntryPoint;
import me.cutehammond.pill.global.oauth.filter.JwtAuthenticationFilter;
import me.cutehammond.pill.global.oauth.handler.OAuth2AuthFailureHandler;
import me.cutehammond.pill.global.oauth.handler.OAuth2AuthSuccessHandler;
import me.cutehammond.pill.global.oauth.handler.TokenAccessDeniedHandler;
import me.cutehammond.pill.global.oauth.repository.OAuth2AuthorizationRequestRepository;
import me.cutehammond.pill.global.oauth.service.CustomOAuth2UserService;
import me.cutehammond.pill.global.oauth.token.AuthTokenProvider;
import me.cutehammond.pill.global.oauth.token.JwtAuthenticationProvider;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static me.cutehammond.pill.global.common.PathFactory.*;

import java.util.List;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final CorsProperties corsProperties;
    private final CustomOAuth2UserService oAuth2UserService;

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final AuthTokenProvider authTokenProvider;

    private final OAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository;

    private final TokenAccessDeniedHandler tokenAccessDeniedHandler;
    private final OAuth2AuthSuccessHandler oAuth2AuthSuccessHandler;
    private final OAuth2AuthFailureHandler oAuth2AuthFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CORS 를 활성화하여 프론트 단과의 통신이 가능하도록 한다.
        http.cors();

        // JWT 방식을 사용하기 위해 세션 정책을 무상태(Stateless)로 만든다.
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // JWT 를 통해 기존의 CSRF 방지법을 대체할 수 있다.
        http.csrf().disable();

        // oauth2 login 을 사용하므로 기본적으로 제공하는 formLogin 기능은 비활성화한다.
        http.formLogin().disable();

        // id 와 password 를 저장하지도 않을뿐더러, parameter 로 직접 credential 를 넘기는 방식은 사용하지 않기에 비활성화한다.
        http.httpBasic().disable();

        //
        http.exceptionHandling()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .accessDeniedHandler(tokenAccessDeniedHandler);

        //
        http.authorizeRequests()
                // CORS 요청 관련
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                // Auth Request 는 모두 접근 가능하도록 한다.
                .antMatchers(AUTH.all().path()).permitAll()

                .antMatchers(API.all().path()).hasAnyAuthority(Role.DEFAULT_USER.getKey())
                .antMatchers(API.all().request("admin").all().path()).hasAnyAuthority(Role.ADMIN.getKey())
                .anyRequest().authenticated();

        // OAuth2 를 이용한 Login 세부 설정
        http.oauth2Login()
                // 프론트 단에서 로그인하는 링크가 {baseUri}/auth/login/{provider}?redirect_uri=... 가 된다.
                .authorizationEndpoint()
                .baseUri(AUTH.request("login").path())
                .authorizationRequestRepository(oAuth2AuthorizationRequestRepository)
                .and()

                // {provider} 플랫폼에서 로그인 시 이 baseUri 로 authorization_code 와 함께 redirect 된다.
                // 로그인 실패 시에도 똑같은 링크로 redirect 된다.
                .redirectionEndpoint()
                .baseUri(AUTH.request("callback").all().path())
                .and()

                // 위 redirection 과정에서 user 설정을 위해 아래의 서비스가 호출된다.
                .userInfoEndpoint()
                .userService(oAuth2UserService)
                .and()
                // 로그인 성공과 실패 시 호출될 Handler 를 설정한다.
                .successHandler(oAuth2AuthSuccessHandler)
                .failureHandler(oAuth2AuthFailureHandler);

        // ApplicationManager.authenticate() 과정에서 JwtAuthentication 인증을 담당할 Provider 를 등록한다.
        http
                .authenticationProvider(jwtAuthenticationProvider);

        // Logout 세부 설정
        http.logout()
                .logoutSuccessUrl(FRONT_PATH.path())
                .logoutUrl(AUTH.request("logout").path())
                .deleteCookies(
                        AuthTokenProvider.REFRESH_TOKEN,
                        OAuth2AuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                        OAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME
                )
                .clearAuthentication(true);

        // formLogin 을 사용하지 않는 대신 JWT 검증을 통해 인증하므로 필터를 추가한다.
        http.addFilterAfter(jwtAuthenticationFilter(authTokenProvider, http), LogoutFilter.class);
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthTokenProvider authTokenProvider, HttpSecurity httpSecurity) {
        return new JwtAuthenticationFilter(authTokenProvider, httpSecurity);
    }

    /*
     * CORS Configuration
     * */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource corsConfigSource = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedHeaders(List.of(corsProperties.getAllowedHeaders().split(",")));
        corsConfig.setAllowedMethods(List.of(corsProperties.getAllowedMethods().split(",")));
        corsConfig.setAllowedOrigins(List.of(corsProperties.getAllowedOrigins().split(",")));
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(corsConfig.getMaxAge());

        corsConfigSource.registerCorsConfiguration(ALL.path(), corsConfig);
        return corsConfigSource;
    }

    /*
     * 정적 리소스의 경우 필터를 거치지 않도록 하여 성능을 향상시킨다.
     * */
    @Bean
    public WebSecurityCustomizer configure() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

}

