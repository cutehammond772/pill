package me.cutehammond.pill.global.common;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class PathFactoryTest {

    @Test
    void request() {
        Assertions.assertThat(PathFactory.INDEX.request("request").path()).isEqualTo("/request");
    }
}