package me.cutehammond.pill.global.config.security;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.global.config.properties.AppProperties;
import me.cutehammond.pill.global.oauth.token.AuthTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final AppProperties properties;

    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public AuthTokenProvider jwtProvider() {
        return new AuthTokenProvider(secret, properties);
    }

}
