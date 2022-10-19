package me.cutehammond.pill.domain.category.domain.dao;

import me.cutehammond.pill.domain.category.domain.Category;
import me.cutehammond.pill.domain.pill.domain.Pill;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PillCategoryMappingRepositoryCustom {

    List<Pill> searchPills(Category category, Pageable pageable);

}
