package me.cutehammond.pill.domain.pill.domain.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.pill.domain.Pill;
import org.bson.types.ObjectId;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PillResponse {

    @NonNull
    private final Long pillSequence;

    @NonNull
    private final String title;

    @NonNull
    private final ObjectId rootElement;

    @NonNull
    private final String userId;

    public static PillResponse getResponse(@NonNull Pill pill) {
        return new PillResponse(pill.getPillSequence(), pill.getTitle(), pill.getRootElement(), pill.getUser().getUserId());
    }

}
