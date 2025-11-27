package com.ryuqq.marketplace.application.brand.port.in.command;

import com.ryuqq.marketplace.application.brand.dto.command.ChangeBrandStatusCommand;

public interface ChangeBrandStatusUseCase {
    void execute(ChangeBrandStatusCommand command);
}
