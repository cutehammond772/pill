package me.cutehammond.pill.domain.point.domain.dto.request;

import lombok.*;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public final class PillPointSpecRequest {

    @NonNull
    private final int point;

    @NonNull
    private final String name;

    /* if null, there is no expiry. */
    private final LocalDateTime expirationDate;

}
