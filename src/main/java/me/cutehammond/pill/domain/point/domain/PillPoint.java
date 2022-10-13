package me.cutehammond.pill.domain.point.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "PILL_POINT_TABLE")
public class PillPoint {

    @Column(name = "point", columnDefinition = "INTEGER DEFAULT 0")
    private Integer point;

    @Column(nullable = false, name = "name")
    private String name;

    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "receivedDate")
    private LocalDateTime receivedDate;

    @Builder
    public PillPoint(@NonNull Integer point, @NonNull String name) {
        this.point = point;
        this.name = name;
    }

}
