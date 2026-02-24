package com.ryuqq.marketplace.application.legacyproduct.port.in.command;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateStockCommand;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;

/** 레거시 상품 재고 수정 UseCase. */
public interface LegacyProductUpdateStockUseCase {

    ProductGroupDetailCompositeResult execute(LegacyUpdateStockCommand command);
}
