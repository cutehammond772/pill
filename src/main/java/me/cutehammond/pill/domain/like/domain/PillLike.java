package me.cutehammond.pill.domain.like.domain;

import lombok.*;
import me.cutehammond.pill.domain.pill.domain.Pill;
import me.cutehammond.pill.domain.user.domain.User;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PILL_LIKE_TABLE")
public class PillLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comment")
    private Pill pill;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @Builder
    public PillLike(@NonNull Pill pill, @NonNull User user) {
        this.pill = pill;
        this.user = user;
    }

}

