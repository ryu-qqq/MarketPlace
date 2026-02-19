package com.ryuqq.marketplace.application.externalbrandmapping.port.in.command;

import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.BatchRegisterExternalBrandMappingCommand;
import java.util.List;

/** 외부 브랜드 매핑 일괄 등록 UseCase. */
public interface BatchRegisterExternalBrandMappingUseCase {

    List<Long> execute(BatchRegisterExternalBrandMappingCommand command);
}
