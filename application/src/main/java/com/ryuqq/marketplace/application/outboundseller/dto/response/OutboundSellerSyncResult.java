package com.ryuqq.marketplace.application.outboundseller.dto.response;

public record OutboundSellerSyncResult(
        boolean success, boolean retryable, String errorCode, String errorMessage) {
    public static OutboundSellerSyncResult ofSuccess() {
        return new OutboundSellerSyncResult(true, false, null, null);
    }

    public static OutboundSellerSyncResult retryableFailure(String errorCode, String errorMessage) {
        return new OutboundSellerSyncResult(false, true, errorCode, errorMessage);
    }

    public static OutboundSellerSyncResult nonRetryableFailure(
            String errorCode, String errorMessage) {
        return new OutboundSellerSyncResult(false, false, errorCode, errorMessage);
    }
}
