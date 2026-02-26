package com.ryuqq.marketplace.application.categorypreset.manager;

import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetDetailResult;
import com.ryuqq.marketplace.application.categorypreset.port.out.query.CategoryPresetCompositionQueryPort;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** CategoryPreset Composition ReadManager. */
@Component
public class CategoryPresetCompositionReadManager {

    private final CategoryPresetCompositionQueryPort compositionQueryPort;

    public CategoryPresetCompositionReadManager(
            CategoryPresetCompositionQueryPort compositionQueryPort) {
        this.compositionQueryPort = compositionQueryPort;
    }

    @Transactional(readOnly = true)
    public CategoryPresetDetailResult getDetail(Long id) {
        return compositionQueryPort
                .findDetailById(id)
                .orElseThrow(() -> new CategoryPresetNotFoundException(id));
    }
}
