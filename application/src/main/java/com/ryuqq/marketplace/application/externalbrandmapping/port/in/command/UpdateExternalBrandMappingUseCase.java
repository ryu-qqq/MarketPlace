package com.ryuqq.marketplace.application.externalbrandmapping.port.in.command;

import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.UpdateExternalBrandMappingCommand;

/** 외부 브랜드 매핑 수정 UseCase. */
public interface UpdateExternalBrandMappingUseCase {

    void execute(UpdateExternalBrandMappingCommand command);
}
