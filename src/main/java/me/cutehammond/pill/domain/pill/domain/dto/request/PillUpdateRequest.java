package me.cutehammond.pill.domain.pill.domain.dto.request;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public final class PillUpdateRequest {

    @NonNull
    private final Long id;

    @NonNull
    private final String title;

    /* 이후 추가 예정 */

}
