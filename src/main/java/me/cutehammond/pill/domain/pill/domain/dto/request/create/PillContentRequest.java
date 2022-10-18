package me.cutehammond.pill.domain.pill.domain.dto.request.create;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PillContentRequest {

    @NonNull
    private final String content;

}
