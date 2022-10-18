package me.cutehammond.pill.domain.pill.domain.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.pill.domain.PillContent;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PillContentResponse {

    @NonNull
    private final String content;

    public static PillContentResponse from(@NonNull PillContent pillContent) {
        return new PillContentResponse(pillContent.getContent());
    }

}
