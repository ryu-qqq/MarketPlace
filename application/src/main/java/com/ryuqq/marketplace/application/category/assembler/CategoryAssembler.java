package com.ryuqq.marketplace.application.category.assembler;

import com.ryuqq.marketplace.application.category.dto.response.CategoryPageResult;
import com.ryuqq.marketplace.application.category.dto.response.CategoryResult;
import com.ryuqq.marketplace.domain.category.aggregate.Category;
import java.util.List;
import org.springframework.stereotype.Component;

/** Category Assembler. */
@Component
public class CategoryAssembler {

    public CategoryResult toResult(Category category) {
        return CategoryResult.from(category);
    }

    public List<CategoryResult> toResults(List<Category> categories) {
        return categories.stream().map(this::toResult).toList();
    }

    public CategoryPageResult toPageResult(
            List<Category> categories, int page, int size, long totalElements) {
        List<CategoryResult> results = toResults(categories);
        return CategoryPageResult.of(results, page, size, totalElements);
    }
}
