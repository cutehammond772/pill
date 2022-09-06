package me.cutehammond.pill.global.config.security;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.user.application.AuthService;
import me.cutehammond.pill.global.config.properties.AppProperties;
import me.cutehammond.pill.global.config.properties.CorsProperties;
import me.cutehammond.pill.global.oauth.entity.Role;
import me.cutehammond.pill.global.oauth.exception.RestAuthenticationEntryPoint;
import me.cutehammond.pill.global.oauth.filter.JwtAuthenticationFilter;
import me.cutehammond.pill.global.oauth.handler.OAuth2AuthenticationFailureHandler;
import me.cutehammond.pill.global.oauth.handler.OAuth2AuthenticationSuccessHandler;
import me.cutehammond.pill.global.oauth.handler.TokenAccessDeniedHandler;
import me.cutehammond.pill.global.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import me.cutehammond.pill.global.oauth.service.CustomOAuth2UserService;
import me.cutehammond.pill.global.oauth.service.CustomUserDetailsService;
import me.cutehammond.pill.global.oauth.token.AuthTokenProvider;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final CorsProperties corsProperties;
    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final CustomOAuth2UserService oAuth2UserService;
    private final TokenAccessDeniedHandler tokenAccessDeniedHandler;
    private final AuthService authService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Cross-Origin Resource Sharing 을 활성화한다.
        http.cors();

        // oauth2의 경우 UserDetails 대신 OAuth2User 를 사용하지만,
        // spring 내부 로직에서 UserDetails 가 필요할 경우를 대비해 관련 Service 를 등록한다.
        http.userDetailsService(userDetailsService);

        // JWT 방식을 사용하기 위해 세션 정책을 무상태(Stateless)로 만든다.
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // only rest api 개발의 경우 csrf(Cross-Site Request Forgery)가 필요없지만, pill project 는
        // 템플릿 엔진을 이용해 프론트엔드 로직을 만드므로 csrf 가 필요하다.
        http.csrf();

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
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers("/api/**").hasAnyAuthority(Role.DEFAULT_USER.getKey())
                .antMatchers("/api/**/admin/**").hasAnyAuthority(Role.ADMIN.getKey())
                .anyRequest().authenticated();

        //
        http.oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorization")
                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
                .and()

                .redirectionEndpoint()
                .baseUri("/*/oauth2/code/*")
                .and()

                .userInfoEndpoint()
                .userService(oAuth2UserService)
                .and()

                .successHandler(oAuth2AuthenticationSuccessHandler())
                .failureHandler(oAuth2AuthenticationFailureHandler());

        // formLogin 을 사용하지 않는 대신 JWT 검증을 통해 인증하므로 필터를 추가한다.
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /*
     * auth 매니저 설정
     * */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /*
     * security 설정 시, 사용할 인코더 설정
     * */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * 토큰 필터 설정
     * */
    @Bean
    public JwtAuthenticationFilter tokenAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider);
    }

    /*
     * 쿠키 기반 인가 Repository
     * 인가 응답을 연계 하고 검증할 때 사용.
     * */
    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    /*
     * Oauth 인증 성공 핸들러
     * */
    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler(
                tokenProvider,
                appProperties,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                authService
        );
    }

    /*
     * Oauth 인증 실패 핸들러
     * */
    @Bean
    public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
        return new OAuth2AuthenticationFailureHandler(oAuth2AuthorizationRequestBasedOnCookieRepository());
    }

    /*
     * Cors 설정
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

        corsConfigSource.registerCorsConfiguration("/**", corsConfig);
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

