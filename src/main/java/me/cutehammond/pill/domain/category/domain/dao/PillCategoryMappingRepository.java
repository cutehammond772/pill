package me.cutehammond.pill.domain.category.domain.dao;

import me.cutehammond.pill.domain.category.domain.PillCategoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PillCategoryMappingRepository extends JpaRepository<PillCategoryMapping, Long>, PillCategoryMappingRepositoryCustom {

}
