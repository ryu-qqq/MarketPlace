package com.ryuqq.marketplace.application.brandpreset.port.in.command;

import com.ryuqq.marketplace.application.brandpreset.dto.command.DeleteBrandPresetsCommand;

/** 브랜드 프리셋 벌크 삭제 UseCase. */
public interface DeleteBrandPresetsUseCase {
    int execute(DeleteBrandPresetsCommand command);
}
