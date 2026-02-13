package com.ryuqq.marketplace.application.product.port.in.command;

import com.ryuqq.marketplace.application.product.dto.command.ChangeProductStatusCommand;

/** 상품(SKU) 상태 변경 UseCase. */
public interface ChangeProductStatusUseCase {

    void execute(ChangeProductStatusCommand command);
}
