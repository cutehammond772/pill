package me.cutehammond.pill.domain.pill.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PILL_CONTENT_TABLE")
public class PillContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index")
    private PillIndex index;

    @Column(nullable = false, length = 1024)
    private String content;

    @Builder
    public PillContent(@NonNull PillIndex index, @NonNull String content) {
        this.index = index;
        this.content = content;
    }

}
