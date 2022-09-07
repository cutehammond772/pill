package me.cutehammond.pill.domain.pill.domain.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

@Getter
@RequiredArgsConstructor
public final class PillResponse {

    @NonNull
    private final Long pillSequence;

    @NonNull
    private final String title;

    @NonNull
    private final ObjectId rootElement;

    @NonNull
    private final String userId;

}
