package com.ryuqq.marketplace.application.category.port.in.query;

import com.ryuqq.marketplace.application.category.dto.response.CategoryPathResponse;

/**
 * GetCategoryPathUseCase - 카테고리 경로 조회 UseCase (breadcrumb)
 */
public interface GetCategoryPathUseCase {
    CategoryPathResponse getPath(Long categoryId);
}
