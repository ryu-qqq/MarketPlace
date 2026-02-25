package com.ryuqq.marketplace.application.legacyproduct.port.in.command;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyMarkOutOfStockCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.result.LegacyProductGroupDetailResult;

/** 레거시 상품 품절 처리 UseCase. */
public interface LegacyProductMarkOutOfStockUseCase {

    LegacyProductGroupDetailResult execute(LegacyMarkOutOfStockCommand command);
}
