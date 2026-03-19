package com.ryuqq.marketplace.application.cancel.port.out.client;

/**
 * 취소 Outbox SQS 메시지.
 *
 * <p>Application 레이어에서 구성하고, Adapter 레이어에서 직렬화합니다.
 */
public record CancelOutboxMessage(
        Long outboxId, String orderItemId, String outboxType, String claimDomain) {

    public static CancelOutboxMessage of(Long outboxId, String orderItemId, String outboxType) {
        return new CancelOutboxMessage(outboxId, orderItemId, outboxType, "CANCEL");
    }
}
