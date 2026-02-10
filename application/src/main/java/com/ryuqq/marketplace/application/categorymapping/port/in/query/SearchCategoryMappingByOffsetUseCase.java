package com.ryuqq.marketplace.application.categorymapping.port.in.query;

import com.ryuqq.marketplace.application.categorymapping.dto.query.CategoryMappingSearchParams;
import com.ryuqq.marketplace.application.categorymapping.dto.response.CategoryMappingPageResult;

/** 카테고리 매핑 검색 UseCase (Offset 기반 페이징). */
public interface SearchCategoryMappingByOffsetUseCase {
    CategoryMappingPageResult execute(CategoryMappingSearchParams params);
}
