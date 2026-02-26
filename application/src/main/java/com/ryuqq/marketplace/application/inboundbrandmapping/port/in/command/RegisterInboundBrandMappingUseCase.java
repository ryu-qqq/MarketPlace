package com.ryuqq.marketplace.application.inboundbrandmapping.port.in.command;

import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.RegisterInboundBrandMappingCommand;

/** 외부 브랜드 매핑 등록 UseCase. */
public interface RegisterInboundBrandMappingUseCase {

    Long execute(RegisterInboundBrandMappingCommand command);
}
