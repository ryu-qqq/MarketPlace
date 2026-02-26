package com.ryuqq.marketplace.application.product.port.in.command;

import com.ryuqq.marketplace.application.product.dto.command.BatchUpdateProductCommand;

/** 상품(SKU) 배치 가격/재고 수정 UseCase. */
public interface BatchUpdateProductUseCase {

    void execute(BatchUpdateProductCommand command);
}
