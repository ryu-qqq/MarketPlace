package com.ryuqq.marketplace.application.outboundseller.dto.command;

public record ProcessPendingOutboundSellerCommand(int batchSize, int delaySeconds) {
    public static ProcessPendingOutboundSellerCommand of(int batchSize, int delaySeconds) {
        return new ProcessPendingOutboundSellerCommand(batchSize, delaySeconds);
    }
}
