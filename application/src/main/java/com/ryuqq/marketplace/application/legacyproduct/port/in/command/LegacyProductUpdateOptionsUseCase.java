package com.ryuqq.marketplace.application.legacyproduct.port.in.command;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateProductsCommand;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;

/** 레거시 상품 옵션/상품 수정 UseCase. */
public interface LegacyProductUpdateOptionsUseCase {

    ProductGroupDetailCompositeResult execute(LegacyUpdateProductsCommand command);
}
