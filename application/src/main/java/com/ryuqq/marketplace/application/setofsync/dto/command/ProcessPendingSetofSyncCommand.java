package com.ryuqq.marketplace.application.setofsync.dto.command;

public record ProcessPendingSetofSyncCommand(int batchSize, int delaySeconds) {
    public static ProcessPendingSetofSyncCommand of(int batchSize, int delaySeconds) {
        return new ProcessPendingSetofSyncCommand(batchSize, delaySeconds);
    }
}
