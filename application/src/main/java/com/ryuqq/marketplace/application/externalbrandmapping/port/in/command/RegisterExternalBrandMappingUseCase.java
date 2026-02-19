package com.ryuqq.marketplace.application.externalbrandmapping.port.in.command;

import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.RegisterExternalBrandMappingCommand;

/** 외부 브랜드 매핑 등록 UseCase. */
public interface RegisterExternalBrandMappingUseCase {

    Long execute(RegisterExternalBrandMappingCommand command);
}
