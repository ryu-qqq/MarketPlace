package com.ryuqq.marketplace.application.categorypreset.port.in.command;

import com.ryuqq.marketplace.application.categorypreset.dto.command.UpdateCategoryPresetCommand;

/** 카테고리 프리셋 수정 UseCase. */
public interface UpdateCategoryPresetUseCase {
    void execute(UpdateCategoryPresetCommand command);
}
