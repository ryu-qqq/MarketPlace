package com.ryuqq.marketplace.domain.outboundsync.vo;

import java.time.Instant;

/**
 * 상품그룹별 연동 상태 요약 VO.
 *
 * <p>PENDING + PROCESSING은 모두 '대기' 상태로 합산된다.
 *
 * @param completedCount 완료 건수
 * @param failedCount 실패 건수
 * @param pendingCount 대기 건수 (PENDING + PROCESSING)
 * @param lastSyncAt 마지막 연동 완료 일시
 */
public record SyncStatusSummary(
        long completedCount, long failedCount, long pendingCount, Instant lastSyncAt) {

    public long totalCount() {
        return completedCount + failedCount + pendingCount;
    }
}
