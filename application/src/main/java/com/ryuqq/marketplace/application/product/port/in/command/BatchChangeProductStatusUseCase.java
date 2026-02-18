package com.ryuqq.marketplace.application.product.port.in.command;

import com.ryuqq.marketplace.application.product.dto.command.BatchChangeProductStatusCommand;

/** 상품(SKU) 배치 상태 변경 UseCase (ProductGroup 단위). */
public interface BatchChangeProductStatusUseCase {

    void execute(BatchChangeProductStatusCommand command);
}
