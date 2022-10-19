package me.cutehammond.pill.domain.category.application;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.category.domain.Category;
import me.cutehammond.pill.domain.category.domain.PillCategoryMapping;
import me.cutehammond.pill.domain.category.domain.dao.CategoryRepository;
import me.cutehammond.pill.domain.category.domain.dao.PillCategoryMappingRepository;
import me.cutehammond.pill.domain.category.domain.dto.response.CategoryResponse;
import me.cutehammond.pill.domain.category.exception.CategoryNotFoundException;
import me.cutehammond.pill.domain.pill.domain.Pill;
import me.cutehammond.pill.domain.pill.domain.dao.sql.PillRepository;
import me.cutehammond.pill.domain.pill.domain.dto.response.PillResponse;
import me.cutehammond.pill.domain.pill.exception.PillNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final PillCategoryMappingRepository mappingRepository;
    private final PillRepository pillRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoriesIn(long pillId) {
        Pill pill = pillRepository.findById(pillId)
                .orElseThrow(() -> new PillNotFoundException(pillId));

        return pill.getCategories().stream()
                .map(PillCategoryMapping::getCategory)
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional
    public CategoryResponse createCategory(String categoryName) {
        Category category = new Category(categoryName);

        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<PillResponse> getPills(Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        List<Pill> pills = mappingRepository.searchPills(category, pageable);

        return pills.stream().map(PillResponse::from)
                .toList();
    }

}
