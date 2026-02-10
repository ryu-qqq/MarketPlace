package com.ryuqq.marketplace.application.brandpreset.port.in.command;

import com.ryuqq.marketplace.application.brandpreset.dto.command.UpdateBrandPresetCommand;

/** 브랜드 프리셋 수정 UseCase. */
public interface UpdateBrandPresetUseCase {
    void execute(UpdateBrandPresetCommand command);
}
