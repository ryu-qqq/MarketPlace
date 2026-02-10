package com.ryuqq.marketplace.application.categorypreset.assembler;

import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetPageResult;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetResult;
import java.util.List;
import org.springframework.stereotype.Component;

/** CategoryPreset Assembler. */
@Component
public class CategoryPresetAssembler {

    public CategoryPresetPageResult toPageResult(
            List<CategoryPresetResult> results, int page, int size, long totalElements) {
        return CategoryPresetPageResult.of(results, page, size, totalElements);
    }
}
