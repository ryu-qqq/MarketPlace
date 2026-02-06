package com.ryuqq.marketplace.application.category.port.in.query;

import com.ryuqq.marketplace.application.category.dto.query.CategorySearchParams;
import com.ryuqq.marketplace.application.category.dto.response.CategoryPageResult;

/** 카테고리 Offset 기반 검색 UseCase. */
public interface SearchCategoryByOffsetUseCase {
    CategoryPageResult execute(CategorySearchParams params);
}
