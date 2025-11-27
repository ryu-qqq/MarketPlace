package com.ryuqq.marketplace.application.brand.port.in.command;

import com.ryuqq.marketplace.application.brand.dto.command.ConfirmBrandAliasCommand;

public interface ConfirmBrandAliasUseCase {
    void execute(ConfirmBrandAliasCommand command);
}
