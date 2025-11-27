package com.ryuqq.marketplace.application.category.port.in.command;

import com.ryuqq.marketplace.application.category.dto.command.ChangeCategoryStatusCommand;

/**
 * ChangeCategoryStatusUseCase - 카테고리 상태 변경 UseCase
 */
public interface ChangeCategoryStatusUseCase {
    void execute(ChangeCategoryStatusCommand command);
}
