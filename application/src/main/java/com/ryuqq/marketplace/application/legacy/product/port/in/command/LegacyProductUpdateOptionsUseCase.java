package com.ryuqq.marketplace.application.legacy.product.port.in.command;

import com.ryuqq.marketplace.application.legacy.product.dto.command.LegacyUpdateProductsCommand;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;

/** 레거시 상품 옵션/상품 수정 UseCase. */
public interface LegacyProductUpdateOptionsUseCase {

    LegacyProductGroupDetailResult execute(LegacyUpdateProductsCommand command);
}
