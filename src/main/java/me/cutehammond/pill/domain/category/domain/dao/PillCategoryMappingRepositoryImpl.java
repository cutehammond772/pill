package me.cutehammond.pill.domain.category.domain.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.category.domain.Category;
import me.cutehammond.pill.domain.pill.domain.Pill;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static me.cutehammond.pill.domain.category.domain.QPillCategoryMapping.*;

@RequiredArgsConstructor
public class PillCategoryMappingRepositoryImpl implements PillCategoryMappingRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Pill> searchPills(Category category, Pageable pageable) {
        /* orderby 구현 필요 */
        return jpaQueryFactory
                .select(pillCategoryMapping.pill)
                .from(pillCategoryMapping)
                .where(pillCategoryMapping.category.eq(category))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()).fetch();
    }
}
