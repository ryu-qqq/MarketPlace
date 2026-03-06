package com.ryuqq.marketplace.application.outboundseller.dto.command;

public record RecoverTimeoutOutboundSellerCommand(int batchSize, int timeoutSeconds) {
    public static RecoverTimeoutOutboundSellerCommand of(int batchSize, int timeoutSeconds) {
        return new RecoverTimeoutOutboundSellerCommand(batchSize, timeoutSeconds);
    }
}
