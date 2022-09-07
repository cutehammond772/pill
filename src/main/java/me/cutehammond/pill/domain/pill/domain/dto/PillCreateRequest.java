package me.cutehammond.pill.domain.pill.domain.dto;

import lombok.Getter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class PillCreateRequest {

    @NonNull
    private final String title;

    @NonNull
    private final String userId;

}
