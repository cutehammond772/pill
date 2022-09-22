package me.cutehammond.pill.global.utils.cookie;

import org.springframework.http.ResponseCookie;
import org.springframework.util.SerializationUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Stream;

public final class CookieUtil {

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        var cookies = Stream.of(request.getCookies());

        return cookies.filter(c -> c.getName().equals(name)).findFirst();
    }

    public static void addCookie(HttpServletResponse response, CookieRequest request) {
        ResponseCookie cookie = ResponseCookie.from(request.getName(), request.getValue())
                .domain("localhost")
                .sameSite(request.getSecureType().getSameSiteValue())
                .path("/")
                .httpOnly(request.getSecureType().isHttpOnly())
                .maxAge(request.getMaxAge())
                //.secure(true)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie cookie = getCookie(request, name).orElse(null);

        if (cookie == null) // Cookie not found
            return;

        cookie.setValue("");
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }

    public static String serialize(Object obj) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(obj));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
    }

}
