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
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pill")
    private Pill pill;

    @Column(nullable = false, length = 256)
    private String indexName;

    @OneToMany(mappedBy = "index", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<PillContent> contents = new ArrayList<>();

    @Builder
    public PillIndex(@NonNull Pill pill, @NonNull String indexName) {
        this.pill = pill;
        this.indexName = indexName;
    }

}
