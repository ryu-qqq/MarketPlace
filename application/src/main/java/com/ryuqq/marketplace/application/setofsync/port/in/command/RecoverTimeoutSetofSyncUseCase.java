package com.ryuqq.marketplace.application.setofsync.port.in.command;

import com.ryuqq.marketplace.application.setofsync.dto.command.RecoverTimeoutSetofSyncCommand;

public interface RecoverTimeoutSetofSyncUseCase {
    void execute(RecoverTimeoutSetofSyncCommand command);
}
