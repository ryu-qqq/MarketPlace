package com.ryuqq.marketplace.application.categorymapping.port.in.command;

import com.ryuqq.marketplace.application.categorymapping.dto.command.DeleteCategoryMappingCommand;

/** 카테고리 매핑 삭제 UseCase. */
public interface DeleteCategoryMappingUseCase {
    void execute(DeleteCategoryMappingCommand command);
}
