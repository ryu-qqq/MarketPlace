package com.ryuqq.marketplace.application.setofsync.dto.response;

public record SetofSyncResult(
        boolean success, boolean retryable, String errorCode, String errorMessage) {
    public static SetofSyncResult ofSuccess() {
        return new SetofSyncResult(true, false, null, null);
    }

    public static SetofSyncResult retryableFailure(String errorCode, String errorMessage) {
        return new SetofSyncResult(false, true, errorCode, errorMessage);
    }

    public static SetofSyncResult nonRetryableFailure(String errorCode, String errorMessage) {
        return new SetofSyncResult(false, false, errorCode, errorMessage);
    }
}
