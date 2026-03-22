package com.ryuqq.marketplace.application.legacy.productgroup.port.in.command;

import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupFullCommand;

/** 레거시 상품그룹 전체 수정 UseCase. */
public interface LegacyProductGroupFullUpdateUseCase {

    void execute(UpdateProductGroupFullCommand command);
}
