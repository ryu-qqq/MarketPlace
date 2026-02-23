package com.ryuqq.marketplace.application.inboundbrandmapping.port.in.command;

import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.UpdateInboundBrandMappingCommand;

/** 외부 브랜드 매핑 수정 UseCase. */
public interface UpdateInboundBrandMappingUseCase {

    void execute(UpdateInboundBrandMappingCommand command);
}
