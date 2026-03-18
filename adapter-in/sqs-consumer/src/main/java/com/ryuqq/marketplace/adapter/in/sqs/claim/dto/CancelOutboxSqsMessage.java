package com.ryuqq.marketplace.adapter.in.sqs.claim.dto;

import java.util.Objects;

/**
 * 취소 Outbox SQS 메시지 DTO.
 *
 * @param outboxId Outbox ID
 * @param orderItemId 주문상품 ID
 * @param outboxType Outbox 유형 (SELLER_CANCEL, APPROVE, REJECT)
 * @param claimDomain 클레임 도메인 구분
 */
public record CancelOutboxSqsMessage(
        Long outboxId, String orderItemId, String outboxType, String claimDomain) {

    public CancelOutboxSqsMessage {
        Objects.requireNonNull(outboxId, "outboxId must not be null");
        Objects.requireNonNull(orderItemId, "orderItemId must not be null");
        Objects.requireNonNull(outboxType, "outboxType must not be null");
    }
}
