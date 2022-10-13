package me.cutehammond.pill.domain.pill.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PILL_INDEX_TABLE")
public class PillIndex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "pillIndexNo")
    private Long pillIndexNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pill")
    private Pill pill;

    @Column(nullable = false, name = "indexName", length = 256)
    private String indexName;

    @OneToMany(mappedBy = "index")
    private final List<PillContent> contents = new ArrayList<>();

    @Builder
    public PillIndex(@NonNull Pill pill, @NonNull String indexName) {
        this.pill = pill;
        this.indexName = indexName;
    }

}
