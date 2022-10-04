package me.cutehammond.pill.global.utils.cookie;

import lombok.NonNull;
import org.springframework.http.ResponseCookie;
import org.springframework.util.SerializationUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Stream;

public final class CookieUtil {

    /**
     * 'name' 에 해당하는 Cookie 를 반환합니다.
     */
    public static Optional<CookieResponse> getCookie(HttpServletRequest request, @NonNull String name) {
        var cookies = request.getCookies();

        if (cookies == null)
            return Optional.empty();

        var cookie = Stream.of(cookies)
                .filter(c -> c.getName().equals(name))
                .findFirst();

        if (cookie.isEmpty())
            return Optional.empty();

        CookieResponse cookieResponse = CookieResponse.builder()
                .name(cookie.get().getName())
                .value(cookie.get().getValue())
                .build();

        return Optional.of(cookieResponse);
    }

    /**
     * CookieRequest 내용에 따라 Cookie 를 추가합니다. (= 추가하는 요청을 보냅니다.)
     */
    public static void addCookie(HttpServletResponse response, @NonNull CookieRequest request) {
        ResponseCookie cookie = ResponseCookie.from(request.getName(), request.getValue())
                .domain("localhost")
                .sameSite(request.getSecureType().getSameSiteValue())
                .path("/")
                .httpOnly(request.getSecureType().isHttpOnly())
                .maxAge(request.getMaxAge())
                //.secure(true) [-> https]
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * 'name' 에 해당하는 Cookie 를 삭제합니다. (= 삭제하는 요청을 보냅니다.)
     * @return name 에 해당하는 cookie 가 없을 경우 false를 반환합니다. (기본적으로 true)
     * */
    public static boolean deleteCookie(HttpServletRequest request, HttpServletResponse response, @NonNull String name) {
        var cookie = getCookie(request, name);

        if (cookie.isEmpty()) // Cookie not found
            return false;

        Cookie del = new Cookie(cookie.get().getName(), cookie.get().getValue());
        del.setPath("/");
        del.setMaxAge(0);

        response.addCookie(del);
        return true;
    }

    public static String serialize(@NonNull Object obj) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(obj));
    }

    public static <T> T deserialize(@NonNull CookieResponse cookieResponse, @NonNull Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookieResponse.getValue())));
    }

}
