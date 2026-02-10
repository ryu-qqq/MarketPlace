package com.ryuqq.marketplace.application.categorymapping.assembler;

import com.ryuqq.marketplace.application.categorymapping.dto.response.CategoryMappingPageResult;
import com.ryuqq.marketplace.application.categorymapping.dto.response.CategoryMappingResult;
import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;
import java.util.List;
import org.springframework.stereotype.Component;

/** CategoryMapping Assembler. */
@Component
public class CategoryMappingAssembler {

    public CategoryMappingResult toResult(CategoryMapping categoryMapping) {
        return CategoryMappingResult.from(categoryMapping);
    }

    public List<CategoryMappingResult> toResults(List<CategoryMapping> categoryMappings) {
        return categoryMappings.stream().map(this::toResult).toList();
    }

    public CategoryMappingPageResult toPageResult(
            List<CategoryMapping> categoryMappings, int page, int size, long totalElements) {
        List<CategoryMappingResult> results = toResults(categoryMappings);
        return CategoryMappingPageResult.of(results, page, size, totalElements);
    }
}
