package me.cutehammond.pill.domain.comment.domain;

import lombok.*;
import me.cutehammond.pill.domain.like.domain.CommentLike;
import me.cutehammond.pill.domain.pill.domain.Pill;
import me.cutehammond.pill.domain.user.domain.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "COMMENT_TABLE")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "commentNo")
    private Long commentNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pill")
    private Pill pill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    private User user;

    @Column(nullable = false, name = "comment", length = 256)
    private String comment;

    @OneToMany(mappedBy = "comment")
    private final List<CommentLike> likedUsers = new ArrayList<>();

    @Builder
    public Comment(@NonNull Pill pill, @NonNull String comment) {
        this.pill = pill;
        this.comment = comment;
    }

}
