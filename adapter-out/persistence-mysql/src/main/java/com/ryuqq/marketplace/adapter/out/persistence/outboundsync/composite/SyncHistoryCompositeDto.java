package com.ryuqq.marketplace.adapter.out.persistence.outboundsync.composite;

import java.time.Instant;

/**
 * 연동 이력 Composite 조회 DTO.
 *
 * <p>outbound_sync_outboxes LEFT JOIN seller_sales_channels LEFT JOIN shop LEFT JOIN
 * outbound_products 결과.
 *
 * @param outboxId Outbox ID
 * @param shopName 쇼핑몰명 (shop 테이블)
 * @param accountId 계정 ID (shop 테이블)
 * @param status Outbox 상태 (PENDING/PROCESSING/COMPLETED/FAILED)
 * @param retryCount 재시도 횟수
 * @param errorMessage 에러 메시지
 * @param externalProductId 외부 상품 ID (outbound_products 테이블)
 * @param createdAt 생성일시 (= 요청일시)
 * @param processedAt 처리 완료일시
 */
public record SyncHistoryCompositeDto(
        Long outboxId,
        String shopName,
        String accountId,
        String status,
        int retryCount,
        String errorMessage,
        String externalProductId,
        Instant createdAt,
        Instant processedAt) {}
