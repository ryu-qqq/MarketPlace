package com.ryuqq.marketplace.adapter.in.sqs.outboundsync.dto;

import java.util.Objects;

/**
 * OutboundSync SQS 메시지 DTO.
 *
 * @param outboxId Outbox ID
 * @param productGroupId 상품그룹 ID
 * @param salesChannelId 판매채널 ID
 * @param syncType 연동 타입 (CREATE, UPDATE, DELETE)
 */
public record OutboundSyncSqsMessage(
        Long outboxId, Long productGroupId, Long salesChannelId, String syncType) {

    public OutboundSyncSqsMessage {
        Objects.requireNonNull(outboxId, "outboxId must not be null");
        Objects.requireNonNull(productGroupId, "productGroupId must not be null");
        Objects.requireNonNull(salesChannelId, "salesChannelId must not be null");
        Objects.requireNonNull(syncType, "syncType must not be null");
    }

    public static OutboundSyncSqsMessage of(
            Long outboxId, Long productGroupId, Long salesChannelId, String syncType) {
        return new OutboundSyncSqsMessage(outboxId, productGroupId, salesChannelId, syncType);
    }
}
