package com.ryuqq.marketplace.application.category.port.in.query;

import com.ryuqq.marketplace.application.category.dto.query.CategoryTreeQuery;
import com.ryuqq.marketplace.application.category.dto.response.CategoryTreeResponse;

/**
 * GetCategoryTreeUseCase - 카테고리 트리 조회 UseCase
 */
public interface GetCategoryTreeUseCase {
    CategoryTreeResponse getTree(CategoryTreeQuery query);
}
