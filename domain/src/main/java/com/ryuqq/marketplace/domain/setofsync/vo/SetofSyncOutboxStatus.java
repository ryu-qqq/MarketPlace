package com.ryuqq.marketplace.domain.setofsync.vo;

public enum SetofSyncOutboxStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED;

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isProcessing() {
        return this == PROCESSING;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }

    public boolean isFailed() {
        return this == FAILED;
    }

    public boolean canProcess() {
        return this == PENDING;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED;
    }

    public SetofSyncOutboxStatus nextStatus() {
        return switch (this) {
            case PENDING -> PROCESSING;
            case PROCESSING -> COMPLETED;
            default -> throw new IllegalStateException("No next status for " + this);
        };
    }
}
