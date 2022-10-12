package me.cutehammond.pill.global.utils.cookie.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import me.cutehammond.pill.global.utils.cookie.CookieSecureType;

@Getter
@Builder
public class CookieRequest {

    @NonNull
    private final String name;

    @NonNull
    private final String value;

    private final int maxAge;

    @NonNull
    private final CookieSecureType secureType;

}
