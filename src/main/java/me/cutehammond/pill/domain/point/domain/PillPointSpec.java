package me.cutehammond.pill.domain.point.domain;

import lombok.*;
import me.cutehammond.pill.domain.point.domain.dto.request.PillPointSpecRequest;
import me.cutehammond.pill.domain.point.exception.particular.PillPointSpecCreatingFailedException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * PillPoint 명세를 나타냅니다. <br>
 * User는 이 명세를 토대로 PillPoint를 '발행'하여 적립합니다.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PILL_POINT_SPECS_TABLE")
public class PillPointSpec {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Positive
    private Integer point;

    @Getter
    @NotBlank
    @Column(nullable = false)
    private String name;

    @Getter
    @Future
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expirationDate;

    @Getter
    private boolean noExpiry;

    public PillPointSpec(@NonNull PillPointSpecRequest pillPointSpecRequest) {
        validatePillPointSpec(pillPointSpecRequest);

        this.point = pillPointSpecRequest.getPoint();
        this.name = pillPointSpecRequest.getName();
        this.expirationDate = pillPointSpecRequest.getExpirationDate();
        this.noExpiry = (this.expirationDate == null);
    }

    /**
     * 이 포인트가 만료된 포인트인지 확인합니다.
     */
    public final boolean isExpired() {
        return !noExpiry && LocalDateTime.now().isAfter(expirationDate);
    }

    private void validatePillPointSpec(PillPointSpecRequest pillPointSpecRequest) {
        /* name validation */
        if (!StringUtils.hasText(pillPointSpecRequest.getName()))
            throw new PillPointSpecCreatingFailedException("포인트의 이름이 존재하지 않습니다.", pillPointSpecRequest.getName());

        /* point validation */
        if (pillPointSpecRequest.getPoint() <= 0)
            throw new PillPointSpecCreatingFailedException("포인트 값은 항상 양수여야 합니다.", pillPointSpecRequest.getName());

        /* expiration date validation */
        if (!pillPointSpecRequest.getExpirationDate().isAfter(LocalDateTime.now()))
            throw new PillPointSpecCreatingFailedException("만료 날짜는 명세를 생성할 때보다 뒤여야 합니다.", pillPointSpecRequest.getName());
    }

}
