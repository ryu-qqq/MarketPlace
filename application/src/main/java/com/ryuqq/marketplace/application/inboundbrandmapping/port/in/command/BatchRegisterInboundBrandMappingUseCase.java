package com.ryuqq.marketplace.application.inboundbrandmapping.port.in.command;

import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.BatchRegisterInboundBrandMappingCommand;
import java.util.List;

/** 외부 브랜드 매핑 일괄 등록 UseCase. */
public interface BatchRegisterInboundBrandMappingUseCase {

    List<Long> execute(BatchRegisterInboundBrandMappingCommand command);
}
