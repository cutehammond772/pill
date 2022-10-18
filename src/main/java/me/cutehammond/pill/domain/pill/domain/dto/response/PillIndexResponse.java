package me.cutehammond.pill.domain.pill.domain.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.pill.domain.PillIndex;

import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PillIndexResponse {

    @NonNull
    private final String indexName;

    @NonNull
    private final List<PillContentResponse> contents;

    public static PillIndexResponse from(PillIndex pillIndex) {
        return new PillIndexResponse(pillIndex.getIndexName(),
                pillIndex.getContents().stream().map(PillContentResponse::from).toList());
    }

}
