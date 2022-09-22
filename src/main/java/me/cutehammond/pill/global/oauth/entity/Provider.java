package me.cutehammond.pill.global.oauth.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {
    GOOGLE("sub");

    private final String registrationId;

}
