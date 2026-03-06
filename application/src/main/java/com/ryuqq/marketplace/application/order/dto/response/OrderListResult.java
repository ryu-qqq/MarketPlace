package com.ryuqq.marketplace.application.order.dto.response;

import java.time.Instant;

/**
 * 주문 목록 조회 결과.
 *
 * @param orderId 주문 ID
 * @param orderNumber 주문번호
 * @param status 주문 상태
 * @param salesChannelId 판매채널 ID
 * @param externalOrderNo 외부 주문번호
 * @param buyerName 구매자명
 * @param itemCount 상품 수
 * @param orderedAt 주문일시
 * @param createdAt 생성일시
 */
public record OrderListResult(
        String orderId,
        String orderNumber,
        String status,
        long salesChannelId,
        String externalOrderNo,
        String buyerName,
        int itemCount,
        Instant orderedAt,
        Instant createdAt) {}
