package me.cutehammond.pill.domain.pill.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import me.cutehammond.pill.domain.pill.domain.PillElementType;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class PillElementRequest {

    @NonNull
    private final PillElementType pillElementType;

    @NonNull
    private final String content;

    @NonNull
    private final List<PillElementRequest> siblings = new ArrayList<>();

    @Builder
    public PillElementRequest(PillElementType pillElementType, String content) {
        this.pillElementType = pillElementType;
        this.content = content;
    }

}
