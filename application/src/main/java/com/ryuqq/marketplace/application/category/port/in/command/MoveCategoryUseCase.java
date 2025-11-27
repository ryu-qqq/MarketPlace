package com.ryuqq.marketplace.application.category.port.in.command;

import com.ryuqq.marketplace.application.category.dto.command.MoveCategoryCommand;

/**
 * MoveCategoryUseCase - 카테고리 이동 UseCase
 *
 * <p>카테고리 이동 시 하위 카테고리의 path/depth 일괄 업데이트</p>
 */
public interface MoveCategoryUseCase {
    void execute(MoveCategoryCommand command);
}
