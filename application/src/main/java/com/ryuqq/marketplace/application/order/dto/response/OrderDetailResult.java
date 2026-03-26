package com.ryuqq.marketplace.application.order.dto.response;

import java.time.Instant;
import java.util.List;

/**
 * 주문 상세 조회 결과.
 *
 * @param orderId 주문 ID
 * @param orderNumber 주문번호
 * @param salesChannelId 판매채널 ID
 * @param shopId 샵 ID
 * @param shopCode 샵 코드
 * @param shopName 샵 이름
 * @param externalOrderNo 외부 주문번호
 * @param externalOrderedAt 외부 주문시간
 * @param buyerInfo 구매자 정보
 * @param payment 결제 정보
 * @param items 주문 상품 목록
 * @param cancels 취소 목록
 * @param claims 클레임 목록
 * @param createdAt 생성일시
 * @param updatedAt 수정일시
 */
public record OrderDetailResult(
        String orderId,
        String orderNumber,
        long salesChannelId,
        long shopId,
        String shopCode,
        String shopName,
        String externalOrderNo,
        Instant externalOrderedAt,
        BuyerInfoResult buyerInfo,
        PaymentResult payment,
        List<OrderItemResult> items,
        List<OrderCancelResult> cancels,
        List<OrderClaimResult> claims,
        Instant createdAt,
        Instant updatedAt) {

    /**
     * 구매자 정보 조회 결과.
     *
     * @param buyerName 구매자명
     * @param email 이메일
     * @param phoneNumber 전화번호
     */
    public record BuyerInfoResult(String buyerName, String email, String phoneNumber) {}
}
