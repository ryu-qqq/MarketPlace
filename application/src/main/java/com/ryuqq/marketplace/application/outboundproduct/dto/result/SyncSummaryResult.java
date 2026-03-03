package com.ryuqq.marketplace.application.outboundproduct.dto.result;

import java.time.Instant;

/**
 * 상품별 연동 통계 결과 DTO.
 *
 * @param totalSyncCount 전체 연동 횟수
 * @param successCount 성공 횟수
 * @param failCount 실패 횟수
 * @param pendingCount 대기 횟수
 * @param lastSyncAt 마지막 연동 일시
 */
public record SyncSummaryResult(
        long totalSyncCount,
        long successCount,
        long failCount,
        long pendingCount,
        Instant lastSyncAt) {}
