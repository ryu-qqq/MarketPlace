package com.ryuqq.marketplace.application.category.port.in.command;

import com.ryuqq.marketplace.application.category.dto.command.UpdateCategoryCommand;
import com.ryuqq.marketplace.application.category.dto.response.CategoryResponse;

/**
 * UpdateCategoryUseCase - 카테고리 수정 UseCase
 */
public interface UpdateCategoryUseCase {
    CategoryResponse execute(UpdateCategoryCommand command);
}
