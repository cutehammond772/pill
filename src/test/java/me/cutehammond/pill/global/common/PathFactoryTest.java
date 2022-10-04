package me.cutehammond.pill.global.common;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class PathFactoryTest {

    @Test
    void testCreatingRequestPath() {
        // given
        String requestWithSlash = "/request";
        String request = "request";

        String requestWithDash = "/re-quest";

        // when
        PathFactory path01 = PathFactory.INDEX.request(requestWithSlash);
        PathFactory path02 = PathFactory.INDEX.request(request);
        PathFactory path03 = PathFactory.INDEX.request(requestWithDash);

        // then
        Assertions.assertThat(path01.path()).isEqualTo(requestWithSlash);
        Assertions.assertThat(path02.path()).isEqualTo(requestWithSlash);
        Assertions.assertThat(path03.path()).isEqualTo(requestWithDash);
    }

    @Test
    void testRegexpMissMatch() {
        // given
        String wrongRequest01 = "/Request";
        String wrongRequest02 = "/request101";
        String wrongRequest03 = "/Request101";


        // then {when}
        Assertions.assertThatThrownBy(() -> {
            PathFactory.INDEX.request(wrongRequest01);
        }).isInstanceOf(IllegalArgumentException.class);

        Assertions.assertThatThrownBy(() -> {
            PathFactory.INDEX.request(wrongRequest02);
        }).isInstanceOf(IllegalArgumentException.class);

        Assertions.assertThatThrownBy(() -> {
            PathFactory.INDEX.request(wrongRequest03);
        }).isInstanceOf(IllegalArgumentException.class);
    }

}