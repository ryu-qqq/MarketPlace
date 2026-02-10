package com.ryuqq.marketplace.application.categorymapping.port.in.command;

import com.ryuqq.marketplace.application.categorymapping.dto.command.RegisterCategoryMappingCommand;

/** 카테고리 매핑 등록 UseCase. */
public interface RegisterCategoryMappingUseCase {
    Long execute(RegisterCategoryMappingCommand command);
}
