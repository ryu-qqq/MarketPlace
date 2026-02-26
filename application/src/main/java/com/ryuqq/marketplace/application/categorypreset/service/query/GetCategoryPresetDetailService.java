package com.ryuqq.marketplace.application.categorypreset.service.query;

import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetDetailResult;
import com.ryuqq.marketplace.application.categorypreset.manager.CategoryPresetCompositionReadManager;
import com.ryuqq.marketplace.application.categorypreset.port.in.query.GetCategoryPresetDetailUseCase;
import org.springframework.stereotype.Service;

/** 카테고리 프리셋 상세 조회 Service. */
@Service
public class GetCategoryPresetDetailService implements GetCategoryPresetDetailUseCase {

    private final CategoryPresetCompositionReadManager compositionReadManager;

    public GetCategoryPresetDetailService(
            CategoryPresetCompositionReadManager compositionReadManager) {
        this.compositionReadManager = compositionReadManager;
    }

    @Override
    public CategoryPresetDetailResult execute(Long categoryPresetId) {
        return compositionReadManager.getDetail(categoryPresetId);
    }
}
