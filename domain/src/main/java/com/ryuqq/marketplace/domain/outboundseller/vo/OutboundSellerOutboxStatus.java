package com.ryuqq.marketplace.domain.outboundseller.vo;

public enum OutboundSellerOutboxStatus {
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

    public OutboundSellerOutboxStatus nextStatus() {
        return switch (this) {
            case PENDING -> PROCESSING;
            case PROCESSING -> COMPLETED;
            default -> throw new IllegalStateException("No next status for " + this);
        };
    }
}
