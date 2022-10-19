package me.cutehammond.pill.domain.category.api;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.category.application.CategoryService;
import me.cutehammond.pill.domain.category.domain.dto.response.CategoryResponse;
import me.cutehammond.pill.domain.category.exception.CategoryInvalidPageRequestException;
import me.cutehammond.pill.domain.category.exception.InvalidCategoryException;
import me.cutehammond.pill.domain.pill.domain.dto.response.PillResponse;
import me.cutehammond.pill.domain.pill.exception.InvalidPillException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping(value = {"/", "/{pillId}"})
    public ResponseEntity<List<CategoryResponse>> getCategories(@PathVariable(required = false) Optional<Long> pillId) {
        if (pillId.isPresent() && pillId.get() < 0)
            throw new InvalidPillException(pillId.get());

        return ResponseEntity.ok(
                pillId.isPresent() ?
                        categoryService.getCategoriesIn(pillId.get()) :
                        categoryService.getAllCategories()
        );
    }

    @GetMapping("/pills/{categoryId}")
    public ResponseEntity<List<PillResponse>> getPills(@PathVariable Long categoryId,
                                                       @RequestParam(name = "page") Integer page,
                                                       @RequestParam(name = "size") Integer size) {
        if (!(page >= 0 && size > 0))
            throw new CategoryInvalidPageRequestException(categoryId, page, size);

        return ResponseEntity.ok(
                categoryService.getPills(categoryId, PageRequest.of(page, size))
        );
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody String categoryName) {
        if (!StringUtils.hasText(categoryName))
            throw new InvalidCategoryException(categoryName);

        categoryService.createCategory(categoryName);
        return ResponseEntity.ok().build();
    }

}
