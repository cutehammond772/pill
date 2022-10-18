package me.cutehammond.pill.domain.pill.domain.dto.request.create;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PillIndexRequest {

    @NonNull
    private final String indexName;

    @NonNull
    private final List<PillContentRequest> contents;

}
