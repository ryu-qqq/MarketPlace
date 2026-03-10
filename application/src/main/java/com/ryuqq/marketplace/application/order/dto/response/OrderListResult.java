package com.ryuqq.marketplace.application.order.dto.response;

import java.time.Instant;

/**
 * 주문 목록 조회 결과.
 *
 * @param orderId 주문 ID
 * @param orderNumber 주문번호
 * @param status 주문 상태
 * @param salesChannelId 판매채널 ID
 * @param shopId 샵 ID
 * @param shopCode 샵 코드
 * @param shopName 샵 이름
 * @param externalOrderNo 외부 주문번호
 * @param externalOrderedAt 외부 주문일시
 * @param buyerName 구매자명
 * @param buyerEmail 구매자 이메일
 * @param buyerPhone 구매자 전화번호
 * @param paymentStatus 결제 상태
 * @param paymentMethod 결제 수단
 * @param paymentAmount 결제 금액
 * @param paidAt 결제일시
 * @param itemCount 상품 수
 * @param createdAt 생성일시
 * @param updatedAt 수정일시
 */
public record OrderListResult(
        String orderId,
        String orderNumber,
        String status,
        long salesChannelId,
        long shopId,
        String shopCode,
        String shopName,
        String externalOrderNo,
        Instant externalOrderedAt,
        String buyerName,
        String buyerEmail,
        String buyerPhone,
        String paymentStatus,
        String paymentMethod,
        int paymentAmount,
        Instant paidAt,
        long itemCount,
        Instant createdAt,
        Instant updatedAt) {}
