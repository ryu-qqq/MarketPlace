package com.ryuqq.marketplace.application.shop.port.in.command;

import com.ryuqq.marketplace.application.shop.dto.command.UpdateShopCommand;

/** Shop 수정 UseCase. */
public interface UpdateShopUseCase {
    void execute(UpdateShopCommand command);
}
