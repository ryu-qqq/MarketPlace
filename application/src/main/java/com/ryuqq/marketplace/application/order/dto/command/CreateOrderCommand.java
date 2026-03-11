package com.ryuqq.marketplace.application.order.dto.command;

import java.time.Instant;
import java.util.List;

/**
 * 주문 생성 Command.
 *
 * @param salesChannelId 판매채널 ID
 * @param shopId 샵 ID
 * @param shopCode 샵 코드 (nullable)
 * @param shopName 샵 이름 (nullable)
 * @param externalOrderNo 외부 주문번호
 * @param externalOrderedAt 외부 주문시간
 * @param buyerName 구매자명
 * @param buyerEmail 구매자 이메일
 * @param buyerPhone 구매자 전화번호
 * @param paymentMethod 결제 수단 (nullable)
 * @param totalPaymentAmount 총 결제 금액
 * @param paidAt 결제 완료 시각 (nullable)
 * @param items 주문 상품 목록
 * @param changedBy 변경자
 */
public record CreateOrderCommand(
        long salesChannelId,
        long shopId,
        String shopCode,
        String shopName,
        String externalOrderNo,
        Instant externalOrderedAt,
        String buyerName,
        String buyerEmail,
        String buyerPhone,
        String paymentMethod,
        int totalPaymentAmount,
        Instant paidAt,
        List<CreateOrderItemCommand> items,
        String changedBy) {}
