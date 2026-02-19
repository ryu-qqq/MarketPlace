package com.ryuqq.marketplace.application.externalcategorymapping.port.in.command;

import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.BatchRegisterExternalCategoryMappingCommand;
import java.util.List;

/** 외부 카테고리 매핑 일괄 등록 UseCase. */
public interface BatchRegisterExternalCategoryMappingUseCase {

    List<Long> execute(BatchRegisterExternalCategoryMappingCommand command);
}
