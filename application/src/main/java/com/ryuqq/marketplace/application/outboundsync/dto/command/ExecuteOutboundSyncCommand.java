package com.ryuqq.marketplace.application.outboundsync.dto.command;

import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import java.util.Objects;

/**
 * 외부 채널 연동 실행 명령 (SQS 컨슈머에서 수신).
 *
 * @param outboxId Outbox ID
 * @param productGroupId 상품그룹 ID
 * @param salesChannelId 판매채널 ID
 * @param syncType 연동 타입
 */
public record ExecuteOutboundSyncCommand(
        Long outboxId, Long productGroupId, Long salesChannelId, SyncType syncType) {

    public ExecuteOutboundSyncCommand {
        Objects.requireNonNull(outboxId, "outboxId must not be null");
        Objects.requireNonNull(productGroupId, "productGroupId must not be null");
        Objects.requireNonNull(salesChannelId, "salesChannelId must not be null");
        Objects.requireNonNull(syncType, "syncType must not be null");
    }

    public static ExecuteOutboundSyncCommand of(
            Long outboxId, Long productGroupId, Long salesChannelId, SyncType syncType) {
        return new ExecuteOutboundSyncCommand(outboxId, productGroupId, salesChannelId, syncType);
    }
}
