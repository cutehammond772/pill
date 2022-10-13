package me.cutehammond.pill.domain.category.domain;

import lombok.*;
import me.cutehammond.pill.domain.pill.domain.Pill;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PILL_CATEGORY_TABLE")
public class PillCategoryMappingTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "pillCategoryNo")
    private Long pillCategoryNo;

    @ManyToOne
    @JoinColumn(name = "pill")
    private Pill pill;

    @ManyToOne
    @JoinColumn(name = "category")
    private Category category;

    @Builder
    public PillCategoryMappingTable(@NonNull Pill pill, @NonNull Category category) {
        this.pill = pill;
        this.category = category;
    }

}
