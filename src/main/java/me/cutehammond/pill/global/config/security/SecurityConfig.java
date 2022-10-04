package me.cutehammond.pill.global.config.security;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.global.config.properties.CorsProperties;
import me.cutehammond.pill.global.oauth.entity.Role;
import me.cutehammond.pill.global.oauth.exception.RestAuthenticationEntryPoint;
import me.cutehammond.pill.global.oauth.filter.JwtAuthenticationFilter;
import me.cutehammond.pill.global.oauth.handler.OAuth2AuthFailureHandler;
import me.cutehammond.pill.global.oauth.handler.OAuth2AuthSuccessHandler;
import me.cutehammond.pill.global.oauth.handler.TokenAccessDeniedHandler;
import me.cutehammond.pill.global.oauth.repository.OAuth2AuthorizationRequestRepository;
import me.cutehammond.pill.global.oauth.service.CustomOAuth2UserService;
import me.cutehammond.pill.global.oauth.auth.AuthTokenProvider;
import me.cutehammond.pill.global.oauth.auth.JwtAuthenticationProvider;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
        // 이 방식을 사용할 경우 매 요청마다 Authentication 을 설정해주어야 한다.
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // JWT 를 통해 기존의 CSRF 방지법을 대체할 수 있다.
        http.csrf().disable();

        // OAuth2 + JWT 기반 Custom Login 을 사용하므로 기본적으로 제공하는 formLogin 기능은 비활성화한다.
        http.formLogin().disable();

        // id 와 password 를 저장하지도 않을뿐더러, parameter 로 직접 credential 을 넘기는 방식은 사용하지 않기에 비활성화한다.
        http.httpBasic().disable();

        /* 예외 처리 관련 Handler 이다. 이후 예외 처리 페이지를 만들 때 사용될 것이다. */
        http.exceptionHandling()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .accessDeniedHandler(tokenAccessDeniedHandler);

        /* 좀 더 세부적으로 권한 시스템을 다듬을 필요가 있다. */
        http.authorizeRequests()
                // CORS 설정
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                // Auth Request 는 모두 접근 가능하도록 한다.
                .antMatchers(AUTH.all().path()).permitAll() /* /auth/** */
                // 기본적인 API 요청은 일반 사용자 권한만 있으면 된다.
                .antMatchers(API.all().path()).hasAnyAuthority(Role.DEFAULT_USER.getKey()) /* /api/** */
                // 관리자 API 요청은 관리자 권한을 가진 사용자만 요청할 수 있다.
                .antMatchers(API.all().request("admin").all().path()).hasAnyAuthority(Role.ADMIN.getKey()) // /api/**/admin/**
                // 그 이외의 요청은 아무튼 인증되어야 한다.
                .anyRequest().authenticated();

        // OAuth2 Login 세부 설정
        http.oauth2Login()
                // 프론트 단에서 로그인하는 링크가 {host}/auth/login/{provider}?redirect_uri=... 가 된다.
                .authorizationEndpoint()
                .baseUri(AUTH.request("login").path()) /* /auth/login */
                .authorizationRequestRepository(oAuth2AuthorizationRequestRepository)
                .and()

                // {provider} 플랫폼에서 로그인 시,
                // {host}/auth/callback/{provider}로 authorization_code 와 함께 redirect 된다.
                // 로그인 실패 시에도 똑같은 링크로 redirect 된다.
                .redirectionEndpoint()
                .baseUri(AUTH.request("callback").all().path()) /* /auth/callback/** */
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
                .logoutUrl(AUTH.request("logout").path()) /* /auth/logout */
                .deleteCookies(
                        AuthTokenProvider.REFRESH_TOKEN,
                        OAuth2AuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                        OAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME
                )
                .clearAuthentication(true);

        // AuthenticationManager 를 직접 Build 하여 JwtAuthenticationFilter 에 Inject 한다.
        /*
            1. JwtAuthenticationFilter 는 Authenticate 를 수행하므로 AuthenticationManager(Bean or Instance)가 필요하다.
            2. 이 Filter 는 SecurityFilterChain (Bean) 이 만들어지기 전에 FilterChain 에 추가되어야 한다.
            3. 그런데 AuthenticationManager 는 SecurityFilterChain Building Process 에서 Build 된다.
            4. 즉 Filter 추가 당시에는 AuthenticationManager 가 존재하지 않으므로,
            내부 로직에서 직접 가져와서 대신 Build 한 다음 각각 (JwtAuthenticationFilter, SecurityFilterChain)에 Inject 해야 한다.
         */
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        http.authenticationManager(authenticationManager);

        // formLogin 을 사용하지 않는 대신 JWT 검증을 통해 인증하므로 필터를 추가한다.
        http.addFilterAfter(new JwtAuthenticationFilter(authTokenProvider, authenticationManager), LogoutFilter.class);
        return http.build();
    }

    /* CORS 관련 설정이다. 이후에 좀 더 엄격하게 범위를 조정할 필요가 있다. */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource corsConfigSource = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedHeaders(List.of(corsProperties.getAllowedHeaders().split(",")));
        corsConfig.setAllowedMethods(List.of(corsProperties.getAllowedMethods().split(",")));
        corsConfig.setAllowedOrigins(List.of(corsProperties.getAllowedOrigins().split(",")));
        corsConfig.setMaxAge(corsConfig.getMaxAge());

        // Cookie 를 받기 위해서는 이 작업이 필요하다.
        corsConfig.setAllowCredentials(true);

        corsConfigSource.registerCorsConfiguration(ALL.path(), corsConfig);
        return corsConfigSource;
    }

    /* 정적 리소스의 경우 Filter 를 거치지 않도록 하여 성능을 향상시킨다. */
    @Bean
    public WebSecurityCustomizer configure() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

}

