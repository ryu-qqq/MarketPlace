package com.ryuqq.marketplace.application.categorypreset.port.in.query;

import com.ryuqq.marketplace.application.categorypreset.dto.query.CategoryPresetSearchParams;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetPageResult;

/** 카테고리 프리셋 목록 조회 UseCase. */
public interface SearchCategoryPresetByOffsetUseCase {
    CategoryPresetPageResult execute(CategoryPresetSearchParams params);
}
