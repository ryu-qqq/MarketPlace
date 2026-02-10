package com.ryuqq.marketplace.application.brandpreset.port.in.command;

import com.ryuqq.marketplace.application.brandpreset.dto.command.RegisterBrandPresetCommand;

/** 브랜드 프리셋 등록 UseCase. */
public interface RegisterBrandPresetUseCase {
    Long execute(RegisterBrandPresetCommand command);
}
