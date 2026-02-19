package com.ryuqq.marketplace.application.externalcategorymapping.port.in.command;

import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.UpdateExternalCategoryMappingCommand;

/** 외부 카테고리 매핑 수정 UseCase. */
public interface UpdateExternalCategoryMappingUseCase {

    void execute(UpdateExternalCategoryMappingCommand command);
}
