package me.cutehammond.pill.domain.point.domain.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;

@Getter
@Builder
public final class PillPointSpecRequest {

    private final int point;

    @NonNull
    private final String name;

    @NonNull
    private final LocalDateTime expirationDate;

}
