package me.cutehammond.pill.global.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PathFactory 단위 테스트")
class PathFactoryTest {

    @Test
    @DisplayName("테스트 - 올바른 규칙으로 생성된 Path")
    void testProperPath() {
        /* given */
        String requestWithSlash = "/request";
        String request = "request";

        String requestWithDash = "/re-quest";

        /* when */
        PathFactory path01 = PathFactory.INDEX.request(requestWithSlash);
        PathFactory path02 = PathFactory.INDEX.request(request);
        PathFactory path03 = PathFactory.INDEX.request(requestWithDash);

        /* then */
        assertThat(path01.path()).isEqualTo(requestWithSlash);
        assertThat(path02.path()).isEqualTo(requestWithSlash);
        assertThat(path03.path()).isEqualTo(requestWithDash);
    }

    @Test
    @DisplayName("테스트 - 규칙을 어긴 Path 생성")
    void testWrongPath() {
        /* given */
        String wrongRequest01 = "/Request";
        String wrongRequest02 = "/request101";
        String wrongRequest03 = "/Request101";


        /* then {when} */
        assertThatThrownBy(() -> {
            PathFactory.INDEX.request(wrongRequest01);
        }).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> {
            PathFactory.INDEX.request(wrongRequest02);
        }).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> {
            PathFactory.INDEX.request(wrongRequest03);
        }).isInstanceOf(IllegalArgumentException.class);
    }

}