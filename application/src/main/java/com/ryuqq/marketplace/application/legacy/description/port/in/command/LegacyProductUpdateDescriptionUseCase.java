package com.ryuqq.marketplace.application.legacy.description.port.in.command;

import com.ryuqq.marketplace.application.legacy.description.dto.command.LegacyUpdateDescriptionCommand;

/** 레거시 상품 상세설명 수정 UseCase. */
public interface LegacyProductUpdateDescriptionUseCase {

    void execute(LegacyUpdateDescriptionCommand command);
}
