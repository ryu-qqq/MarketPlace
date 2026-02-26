package com.ryuqq.marketplace.application.legacy.productgroup.port.in.command;

import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyMarkOutOfStockCommand;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;

/** 레거시 상품 품절 처리 UseCase. */
public interface LegacyProductMarkOutOfStockUseCase {

    LegacyProductGroupDetailResult execute(LegacyMarkOutOfStockCommand command);
}
