package com.ryuqq.marketplace.application.brandmapping.port.in.command;

import com.ryuqq.marketplace.application.brandmapping.dto.command.RegisterBrandMappingCommand;

/** 브랜드 매핑 등록 UseCase. */
public interface RegisterBrandMappingUseCase {
    Long execute(RegisterBrandMappingCommand command);
}
