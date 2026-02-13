package com.ryuqq.marketplace.application.product.port.in.command;

import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;

/** 상품(SKU) 재고 수정 UseCase. */
public interface UpdateProductStockUseCase {

    void execute(UpdateProductStockCommand command);
}
