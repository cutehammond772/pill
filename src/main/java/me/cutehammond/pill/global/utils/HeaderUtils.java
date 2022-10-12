package me.cutehammond.pill.global.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public final class HeaderUtils {

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    public static Optional<String> getAccessToken(HttpServletRequest request) {
        var header = Optional.ofNullable(request.getHeader(HEADER_AUTHORIZATION));

        return header
                .filter(s -> s.startsWith(TOKEN_PREFIX))
                .map(s -> s.substring(TOKEN_PREFIX.length()));
    }

}
