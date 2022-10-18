package me.cutehammond.pill.domain.pill.domain.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.pill.domain.Pill;

import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PillResponse {

    @NonNull
    private final String title;

    @NonNull
    private final List<PillIndexResponse> indexes;

    public static PillResponse from(@NonNull Pill pill) {
        return new PillResponse(pill.getTitle(),
                pill.getIndexes().stream().map(PillIndexResponse::from).toList());
    }

}
