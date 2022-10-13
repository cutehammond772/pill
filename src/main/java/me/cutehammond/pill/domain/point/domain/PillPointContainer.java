package me.cutehammond.pill.domain.point.domain;

import lombok.*;
import me.cutehammond.pill.domain.user.domain.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PILL_POINT_CONTAINER_TABLE")
public class PillPointContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "pillPointContainerNo")
    private Long pillPointContainerNo;

    @OneToOne
    @JoinColumn(name = "user")
    private User user;

    @ElementCollection
    private final List<PillPoint> points = new ArrayList<>();

    @Builder
    public PillPointContainer(@NonNull User user) {
        this.user = user;
    }

}
