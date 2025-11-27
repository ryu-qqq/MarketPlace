package com.ryuqq.marketplace.application.category.port.in.query;

import com.ryuqq.marketplace.application.category.dto.response.CategoryResponse;

import java.util.Optional;

/**
 * GetCategoryUseCase - 단일 카테고리 조회 UseCase
 */
public interface GetCategoryUseCase {

    Optional<CategoryResponse> getById(Long categoryId);

    Optional<CategoryResponse> getByCode(String code);
}
