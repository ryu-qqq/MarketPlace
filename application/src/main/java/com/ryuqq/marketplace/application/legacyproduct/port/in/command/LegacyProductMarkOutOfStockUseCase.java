package com.ryuqq.marketplace.application.legacyproduct.port.in.command;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyMarkOutOfStockCommand;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;

/** 레거시 상품 품절 처리 UseCase. */
public interface LegacyProductMarkOutOfStockUseCase {

    ProductGroupDetailCompositeResult execute(LegacyMarkOutOfStockCommand command);
}
