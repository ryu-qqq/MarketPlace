package com.ryuqq.marketplace.application.externalcategorymapping.port.in.command;

import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.RegisterExternalCategoryMappingCommand;

/** 외부 카테고리 매핑 등록 UseCase. */
public interface RegisterExternalCategoryMappingUseCase {

    Long execute(RegisterExternalCategoryMappingCommand command);
}
