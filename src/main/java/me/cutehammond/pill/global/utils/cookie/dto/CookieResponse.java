package me.cutehammond.pill.global.utils.cookie.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class CookieResponse {

    @NonNull
    private final String name;

    @NonNull
    private final String value;

}
