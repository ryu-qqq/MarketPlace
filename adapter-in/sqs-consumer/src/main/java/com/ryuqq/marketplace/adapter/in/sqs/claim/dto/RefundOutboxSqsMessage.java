package com.ryuqq.marketplace.adapter.in.sqs.claim.dto;

import java.util.Objects;

/**
 * 환불 Outbox SQS 메시지 DTO.
 *
 * @param outboxId Outbox ID
 * @param orderItemId 주문상품 ID
 * @param outboxType Outbox 유형 (REQUEST, APPROVE, REJECT, COMPLETE)
 * @param claimDomain 클레임 도메인 구분
 */
public record RefundOutboxSqsMessage(
        Long outboxId, String orderItemId, String outboxType, String claimDomain) {

    public RefundOutboxSqsMessage {
        Objects.requireNonNull(outboxId, "outboxId must not be null");
        Objects.requireNonNull(orderItemId, "orderItemId must not be null");
        Objects.requireNonNull(outboxType, "outboxType must not be null");
    }
}
