package com.ryuqq.marketplace.application.product.port.in.command;

import com.ryuqq.marketplace.application.product.dto.command.UpdateProductPriceCommand;

/** 상품(SKU) 가격 수정 UseCase. */
public interface UpdateProductPriceUseCase {

    void execute(UpdateProductPriceCommand command);
}
