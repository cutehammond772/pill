package me.cutehammond.pill.domain.point.domain;

import lombok.*;
import me.cutehammond.pill.domain.point.domain.dto.request.PillPointSpecRequest;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
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
    @PositiveOrZero
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

}
