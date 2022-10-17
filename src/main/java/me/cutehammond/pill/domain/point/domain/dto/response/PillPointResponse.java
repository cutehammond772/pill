package me.cutehammond.pill.domain.point.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import me.cutehammond.pill.domain.point.domain.PillPoint;

import java.time.LocalDateTime;

@Getter
@Builder
public final class PillPointResponse {

    private final int receivedPoint;

    private final int pointLeft;

    @NonNull
    private final String name;

    @NonNull
    private final LocalDateTime receivedDate;

    @NonNull
    private final LocalDateTime expirationDate;

    public static PillPointResponse from(PillPoint pillPoint) {
        return new PillPointResponse(pillPoint.getSpec().getPoint(), pillPoint.getPointLeft(),
                pillPoint.getSpec().getName(), pillPoint.getReceivedDate(), pillPoint.getSpec().getExpirationDate());
    }

}
