package com.ryuqq.marketplace.application.brand.port.in.command;

import com.ryuqq.marketplace.application.brand.dto.command.RemoveBrandAliasCommand;

public interface RemoveBrandAliasUseCase {
    void execute(RemoveBrandAliasCommand command);
}
