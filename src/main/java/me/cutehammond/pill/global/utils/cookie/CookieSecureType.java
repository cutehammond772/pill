package me.cutehammond.pill.global.utils.cookie;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CookieSecureType {

    /** 동일한 도메인에서만 주고받을 수 있으며, 프론트 단에서 접근할 수 없습니다. */
    STRICT(true, true),
    /** 동일한 도메인에서만 주고 받을 수 있으며, 프론트 단에서 쿠키에 접근할 수 있습니다. */
    DEFAULT(false, true),
    /** 동일한 도메인 이외의 크로스 도메인에서 주고받을 수 있습니다. <br> 단 읽기만 가능합니다. */
    NONE(false, false);

    public final boolean httpOnly;

    public final boolean sameSiteSecured;

    private static final String SS_STRICT = "Strict";
    private static final String SS_LAX = "Lax";

    public final String getSameSiteValue() {
        return sameSiteSecured ? SS_STRICT : SS_LAX;
    }

}
