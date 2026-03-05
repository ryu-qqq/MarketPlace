package com.ryuqq.marketplace.application.setofsync.dto.command;

public record RecoverTimeoutSetofSyncCommand(int batchSize, int timeoutSeconds) {
    public static RecoverTimeoutSetofSyncCommand of(int batchSize, int timeoutSeconds) {
        return new RecoverTimeoutSetofSyncCommand(batchSize, timeoutSeconds);
    }
}
