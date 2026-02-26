package com.ryuqq.marketplace.application.legacy.product.port.in.command;

import com.ryuqq.marketplace.application.legacy.product.dto.command.LegacyUpdatePriceCommand;

/** 레거시 상품 가격 수정 UseCase. */
public interface LegacyProductUpdatePriceUseCase {

    void execute(LegacyUpdatePriceCommand command);
}
