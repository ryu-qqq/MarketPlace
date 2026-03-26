package com.ryuqq.marketplace.application.legacy.product.port.in.command;

import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;

/** 레거시 상품 옵션/SKU 전체 교체 UseCase. */
public interface LegacyProductUpdateOptionsUseCase {

    void execute(UpdateProductsCommand command);
}
