package me.cutehammond.pill.domain.pill.domain.dto.request.create;

import lombok.Getter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public final class PillCreateRequest {

    @NonNull
    private final String title;

    @NonNull
    private final List<PillIndexRequest> indexes;

    @NonNull
    private final List<PillCategoryMappingRequest> categories;

}
