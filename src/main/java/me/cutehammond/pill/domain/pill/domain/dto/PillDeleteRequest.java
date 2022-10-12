package me.cutehammond.pill.domain.pill.domain.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class PillDeleteRequest {

    private final long id;

}
