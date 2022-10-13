package me.cutehammond.pill.domain.pill.domain;

import lombok.*;
import me.cutehammond.pill.domain.category.domain.PillCategoryMappingTable;
import me.cutehammond.pill.domain.comment.domain.Comment;
import me.cutehammond.pill.domain.like.domain.PillLike;
import me.cutehammond.pill.domain.user.domain.User;
import me.cutehammond.pill.global.common.BaseTimeEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PILL_TABLE")
public class Pill extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "pillNo")
    private Long pillNo;

    /**
     * pill의 작성자를 나타냅니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    private User user;

    /**
     * pill의 제목을 나타냅니다. 수정 가능합니다.
     */
    @Setter
    @Column(nullable = false, name = "title", length = 256)
    private String title;

    /**
     * pill의 조회수를 나타냅니다.
     */
    @Setter
    @Column(name = "views", columnDefinition = "INTEGER DEFAULT 0")
    private Integer views;

    /**
     * 이 Pill이 포함하는 Category들을 나타냅니다.
     */
    @OneToMany(mappedBy = "pill")
    private final List<PillCategoryMappingTable> categories = new ArrayList<>();

    /**
     * 이 Pill의 좋아요 정보를 나타냅니다.
     */
    @OneToMany(mappedBy = "pill")
    private final List<PillLike> likes = new ArrayList<>();

    /**
     * 이 Pill에 작성된 댓글들을 나타냅니다.
     */
    @OneToMany(mappedBy = "pill")
    private final List<Comment> comments = new ArrayList<>();

    /**
     * 이 Pill의 인덱스 모음을 나타냅니다.
     */
    @OneToMany(mappedBy = "pill")
    private final List<PillIndex> indexes = new ArrayList<>();

    @Builder
    public Pill(@NonNull String title, @NonNull User user) {
        this.title = title;
        this.user = user;
    }

}
