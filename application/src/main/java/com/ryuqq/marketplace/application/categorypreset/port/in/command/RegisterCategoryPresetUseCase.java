package com.ryuqq.marketplace.application.categorypreset.port.in.command;

import com.ryuqq.marketplace.application.categorypreset.dto.command.RegisterCategoryPresetCommand;

/** 카테고리 프리셋 등록 UseCase. */
public interface RegisterCategoryPresetUseCase {
    Long execute(RegisterCategoryPresetCommand command);
}
