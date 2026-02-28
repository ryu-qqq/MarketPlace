package com.ryuqq.marketplace.application.outboundsync.dto.command;

/**
 * 외부 채널 연동 실행 명령 (SQS 컨슈머에서 수신).
 *
 * @param outboxId Outbox ID
 * @param productGroupId 상품그룹 ID
 * @param salesChannelId 판매채널 ID
 * @param syncType 연동 타입 (CREATE, UPDATE, DELETE)
 */
public record ExecuteOutboundSyncCommand(
        Long outboxId, Long productGroupId, Long salesChannelId, String syncType) {

    public static ExecuteOutboundSyncCommand of(
            Long outboxId, Long productGroupId, Long salesChannelId, String syncType) {
        return new ExecuteOutboundSyncCommand(outboxId, productGroupId, salesChannelId, syncType);
    }
}
