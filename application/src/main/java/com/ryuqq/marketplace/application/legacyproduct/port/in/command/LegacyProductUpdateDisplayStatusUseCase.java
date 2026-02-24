package com.ryuqq.marketplace.application.legacyproduct.port.in.command;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateDisplayStatusCommand;

/** 레거시 상품 전시 상태 변경 UseCase. */
public interface LegacyProductUpdateDisplayStatusUseCase {

    void execute(LegacyUpdateDisplayStatusCommand command);
}
