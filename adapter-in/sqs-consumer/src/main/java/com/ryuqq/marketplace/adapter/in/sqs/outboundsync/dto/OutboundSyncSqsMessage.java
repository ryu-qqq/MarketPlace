package com.ryuqq.marketplace.adapter.in.sqs.outboundsync.dto;

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

    public static OutboundSyncSqsMessage of(
            Long outboxId, Long productGroupId, Long salesChannelId, String syncType) {
        return new OutboundSyncSqsMessage(outboxId, productGroupId, salesChannelId, syncType);
    }
}
