package com.ryuqq.marketplace.application.categorypreset.port.in.query;

import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetDetailResult;

/** 카테고리 프리셋 상세 조회 UseCase. */
public interface GetCategoryPresetDetailUseCase {
    CategoryPresetDetailResult execute(Long categoryPresetId);
}
