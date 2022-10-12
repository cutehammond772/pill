package me.cutehammond.pill.global.utils.cookie;

import lombok.extern.slf4j.Slf4j;
import me.cutehammond.pill.global.utils.cookie.dto.CookieResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("CookieUtil 테스트")
class CookieUtilsTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletRequest response;

    @Test
    @DisplayName("테스트 - 존재하지 않는 쿠키를 가져왔을 때")
    void testGetCookieAndIsEmpty() {
        /* given */
        given(request.getCookies()).willReturn(new Cookie[] { });
        String cookie = "COOKIE_ANYWAY";

        /* when */
        Optional<CookieResponse> emptyCookie = CookieUtils.getCookie(request, cookie);

        /* then */
        assertThat(emptyCookie).isEmpty();
    }

    @Test
    @DisplayName("테스트 - 특정한 쿠키를 가져왔을 때 동일성 검증")
    void testGetCookieAndValidate() {
        /* given */
        String name = "THAT_COOKIE";
        String value = "VALUE_OF_THAT_COOKIE";
        given(request.getCookies()).willReturn(new Cookie[] { new Cookie(name, value) });

        /* when */
        Optional<CookieResponse> cookieResponse = CookieUtils.getCookie(request, name);

        /* then */
        assertThat(cookieResponse).isNotEmpty();
        assertThat(cookieResponse.map(CookieResponse::getName)).isEqualTo(name);
        assertThat(cookieResponse.map(CookieResponse::getValue)).isEqualTo(value);
    }

}