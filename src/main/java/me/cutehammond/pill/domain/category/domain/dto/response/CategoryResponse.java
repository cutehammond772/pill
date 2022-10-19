package me.cutehammond.pill.domain.category.domain.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.category.domain.Category;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryResponse {

    @NonNull
    private final Long id;

    @NonNull
    private final String name;

    public static CategoryResponse from(@NonNull Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }

}
