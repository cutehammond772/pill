package me.cutehammond.pill.global.utils.cookie;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class CookieRequest {

    @NonNull
    private final String name;

    @NonNull
    private final String value;

    @NonNull
    private final int maxAge;

    @NonNull
    private final CookieSecureType secureType;

}
