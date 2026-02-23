package com.ryuqq.marketplace.application.inboundcategorymapping.port.in.command;

import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.UpdateInboundCategoryMappingCommand;

/** 외부 카테고리 매핑 수정 UseCase. */
public interface UpdateInboundCategoryMappingUseCase {

    void execute(UpdateInboundCategoryMappingCommand command);
}
