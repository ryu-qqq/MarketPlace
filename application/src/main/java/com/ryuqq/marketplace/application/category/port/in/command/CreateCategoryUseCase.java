package com.ryuqq.marketplace.application.category.port.in.command;

import com.ryuqq.marketplace.application.category.dto.command.CreateCategoryCommand;
import com.ryuqq.marketplace.application.category.dto.response.CategoryResponse;

/**
 * CreateCategoryUseCase - 카테고리 생성 UseCase
 */
public interface CreateCategoryUseCase {
    CategoryResponse execute(CreateCategoryCommand command);
}
