package com.ryuqq.marketplace.application.legacy.product.port.in.command;

import com.ryuqq.marketplace.application.legacy.product.dto.command.LegacyUpdateStockCommand;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;

/** 레거시 상품 재고 수정 UseCase. */
public interface LegacyProductUpdateStockUseCase {

    LegacyProductGroupDetailResult execute(LegacyUpdateStockCommand command);
}
