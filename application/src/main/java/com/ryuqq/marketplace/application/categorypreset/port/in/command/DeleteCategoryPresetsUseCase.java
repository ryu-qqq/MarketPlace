package com.ryuqq.marketplace.application.categorypreset.port.in.command;

import com.ryuqq.marketplace.application.categorypreset.dto.command.DeleteCategoryPresetsCommand;

/** 카테고리 프리셋 벌크 삭제 UseCase. */
public interface DeleteCategoryPresetsUseCase {
    int execute(DeleteCategoryPresetsCommand command);
}
