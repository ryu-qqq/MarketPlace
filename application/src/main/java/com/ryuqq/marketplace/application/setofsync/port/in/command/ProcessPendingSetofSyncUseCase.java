package com.ryuqq.marketplace.application.setofsync.port.in.command;

import com.ryuqq.marketplace.application.setofsync.dto.command.ProcessPendingSetofSyncCommand;

public interface ProcessPendingSetofSyncUseCase {
    void execute(ProcessPendingSetofSyncCommand command);
}
