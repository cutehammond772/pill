package me.cutehammond.pill.domain.like.domain;

import lombok.*;
import me.cutehammond.pill.domain.comment.domain.Comment;
import me.cutehammond.pill.domain.user.domain.User;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "COMMENT_LIKE_TABLE")
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "commentLikeNo")
    private Long commentLikeNo;

    @ManyToOne
    @JoinColumn(name = "comment")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @Builder
    public CommentLike(@NonNull Comment comment, @NonNull User user) {
        this.comment = comment;
        this.user = user;
    }

}
