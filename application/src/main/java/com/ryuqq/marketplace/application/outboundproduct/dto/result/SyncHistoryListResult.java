package com.ryuqq.marketplace.application.outboundproduct.dto.result;

import java.time.Instant;

/**
 * 연동 이력 단건 결과 DTO.
 *
 * @param id Outbox ID
 * @param shopName 쇼핑몰명 (Shop에서 조회)
 * @param accountId 계정 ID (Shop에서 조회)
 * @param presetName 프리셋명 (nullable - 현재 Outbox에 미저장)
 * @param status 상태 (PENDING/PROCESSING/COMPLETED/FAILED)
 * @param statusLabel 상태 라벨
 * @param retryCount 재시도 횟수
 * @param errorMessage 에러 메시지
 * @param externalProductId 외부 상품 ID (OutboundProduct에서 조회, nullable)
 * @param requestedAt 요청 일시
 * @param completedAt 완료 일시 (nullable)
 */
public record SyncHistoryListResult(
        long id,
        String shopName,
        String accountId,
        String presetName,
        String status,
        String statusLabel,
        int retryCount,
        String errorMessage,
        String externalProductId,
        Instant requestedAt,
        Instant completedAt) {}
