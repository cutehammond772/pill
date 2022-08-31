package me.cutehammond.pill.global.oauth.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Role {

    DEFAULT_USER("ROLE_USER", "일반 사용자"),
    ADMIN("ROLE_ADMIN", "운영자");

    private final String key;
    private final String name;

    public static final Role of(String key) {
        return Stream.of(Role.values())
                .filter(role -> key.equals(role.getKey()))
                .findFirst()

                /* throws NoSuchElementException */
                .orElseThrow();
    }

}
