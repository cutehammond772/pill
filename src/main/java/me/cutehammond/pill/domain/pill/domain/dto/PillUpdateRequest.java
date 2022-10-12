package me.cutehammond.pill.domain.pill.domain.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public final class PillUpdateRequest {

    @NonNull
    private final String title;

    private final long id;

}
