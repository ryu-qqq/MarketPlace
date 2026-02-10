package com.ryuqq.marketplace.application.brandmapping.port.in.command;

import com.ryuqq.marketplace.application.brandmapping.dto.command.DeleteBrandMappingCommand;

/** 브랜드 매핑 삭제 UseCase. */
public interface DeleteBrandMappingUseCase {
    void execute(DeleteBrandMappingCommand command);
}
