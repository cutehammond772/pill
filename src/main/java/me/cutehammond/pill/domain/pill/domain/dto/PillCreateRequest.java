package me.cutehammond.pill.domain.pill.domain.dto;

import lombok.Getter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import me.cutehammond.pill.domain.user.domain.User;

@Getter
@RequiredArgsConstructor
public final class PillCreateRequest {

    @NonNull
    private final String title;

    @NonNull
    private final User user;

}
