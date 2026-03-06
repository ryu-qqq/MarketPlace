package com.ryuqq.marketplace.application.order.dto.command;

import java.time.Instant;
import java.util.List;

/**
 * 주문 생성 Command.
 *
 * @param salesChannelId 판매채널 ID
 * @param shopId 샵 ID
 * @param externalOrderNo 외부 주문번호
 * @param externalOrderedAt 외부 주문시간
 * @param buyerName 구매자명
 * @param buyerEmail 구매자 이메일
 * @param buyerPhone 구매자 전화번호
 * @param items 주문 상품 목록
 * @param changedBy 변경자
 */
public record CreateOrderCommand(
        long salesChannelId,
        long shopId,
        String externalOrderNo,
        Instant externalOrderedAt,
        String buyerName,
        String buyerEmail,
        String buyerPhone,
        List<CreateOrderItemCommand> items,
        String changedBy) {}
