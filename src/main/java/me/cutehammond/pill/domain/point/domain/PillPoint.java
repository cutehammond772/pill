package me.cutehammond.pill.domain.point.domain;

import lombok.*;
import me.cutehammond.pill.domain.point.exception.PillPointOutOfBoundsException;
import me.cutehammond.pill.domain.point.exception.particular.PillPointAddingFailedException;
import me.cutehammond.pill.domain.point.exception.particular.PillPointUsingFailedException;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

/**
 * PillPointSpec로부터 발행된 포인트입니다. 특정 User에게만 종속됩니다.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
@Table(name = "PILL_POINTS_TABLE")
public class PillPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spec", nullable = false)
    private PillPointSpec spec;

    @Getter
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receivedDate;

    @Getter
    @PositiveOrZero
    private int pointLeft;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pillPointContainer", nullable = false)
    private PillPointContainer pillPointContainer;

    @Builder
    public PillPoint(@NonNull PillPointSpec spec, @NonNull PillPointContainer pillPointContainer) {
        if (spec.isExpired())
            throw new PillPointAddingFailedException(spec.getName());

        this.spec = spec;
        this.pillPointContainer = pillPointContainer;
        this.pointLeft = spec.getPoint();
    }

    public final boolean isRunOut() {
        return pointLeft == 0;
    }

    public final boolean isExpired() {
        return getSpec().isExpired();
    }

    /**
     * 특정 포인트만큼 차감합니다. <br>
     * 기존에 남아있는 포인트보다 초과하여 사용하거나 차감할 포인트가 0 이하이면 PillPointUsingFailedException 예외를 호출합니다.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void usePoint(int point) {
        /* point validation */
        if (this.pointLeft > point || point <= 0)
            throw new PillPointOutOfBoundsException("특정 포인트에서 사용할 포인트의 범위가 정상 범위를 벗어났습니다.");

        this.pointLeft -= point;
    }

}
