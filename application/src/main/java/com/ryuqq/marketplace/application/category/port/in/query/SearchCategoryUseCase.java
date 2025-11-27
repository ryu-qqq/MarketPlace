package com.ryuqq.marketplace.application.category.port.in.query;

import com.ryuqq.marketplace.application.category.dto.query.CategorySearchQuery;
import com.ryuqq.marketplace.application.category.dto.response.CategoryResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SearchCategoryUseCase - 카테고리 검색 UseCase
 */
public interface SearchCategoryUseCase {

    List<CategoryResponse> search(String keyword);

    List<CategoryResponse> searchLeaves(CategorySearchQuery query);

    List<CategoryResponse> findUpdatedSince(LocalDateTime since);
}
