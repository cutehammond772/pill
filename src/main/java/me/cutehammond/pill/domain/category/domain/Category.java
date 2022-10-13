package me.cutehammond.pill.domain.category.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "CATEGORY_TABLE")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "categoryNo")
    private Long categoryNo;

    @Column(nullable = false, name = "name")
    private String name;

    @OneToMany(mappedBy = "category")
    private final List<PillCategoryMappingTable> pills = new ArrayList<>();

    @Builder
    public Category(@NonNull String name) {
        this.name = name;
    }

}
