package com.ryuqq.marketplace.application.legacyproduct.port.in.command;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateStockCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.result.LegacyProductGroupDetailResult;

/** 레거시 상품 재고 수정 UseCase. */
public interface LegacyProductUpdateStockUseCase {

    LegacyProductGroupDetailResult execute(LegacyUpdateStockCommand command);
}
