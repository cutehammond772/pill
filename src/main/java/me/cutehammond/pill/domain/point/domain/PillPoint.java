package me.cutehammond.pill.domain.point.domain;

import lombok.*;
import me.cutehammond.pill.domain.point.exception.PillPointAddingFailedException;
import me.cutehammond.pill.domain.point.exception.PillPointUsingFailedException;
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
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spec")
    private PillPointSpec spec;

    @Getter
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receivedDate;

    @Getter
    @PositiveOrZero
    private int pointLeft;

    public PillPoint(@NonNull PillPointSpec spec) {
        if (spec.isExpired())
            throw new PillPointAddingFailedException();

        this.spec = spec;
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
        if (this.pointLeft > point || point <= 0)
            throw new PillPointUsingFailedException();

        this.pointLeft -= point;
    }

}
