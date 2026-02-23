package com.ryuqq.marketplace.application.inboundcategorymapping.port.in.command;

import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.BatchRegisterInboundCategoryMappingCommand;
import java.util.List;

/** 외부 카테고리 매핑 일괄 등록 UseCase. */
public interface BatchRegisterInboundCategoryMappingUseCase {

    List<Long> execute(BatchRegisterInboundCategoryMappingCommand command);
}
