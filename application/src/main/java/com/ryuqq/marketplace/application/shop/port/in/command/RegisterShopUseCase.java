package com.ryuqq.marketplace.application.shop.port.in.command;

import com.ryuqq.marketplace.application.shop.dto.command.RegisterShopCommand;

/** Shop 등록 UseCase. */
public interface RegisterShopUseCase {
    Long execute(RegisterShopCommand command);
}
