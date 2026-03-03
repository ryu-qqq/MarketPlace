package com.ryuqq.marketplace.application.inboundorder.dto.external;

import java.time.Instant;
import java.util.List;

/**
 * 외부몰 주문 데이터.
 *
 * @param externalOrderNo 외부 주문번호
 * @param orderedAt 주문일시
 * @param buyerName 구매자명
 * @param buyerEmail 구매자 이메일
 * @param buyerPhone 구매자 전화번호
 * @param paymentMethod 결제수단
 * @param totalPaymentAmount 총 결제금액
 * @param paidAt 결제일시
 * @param items 주문 아이템 목록
 */
public record ExternalOrderPayload(
        String externalOrderNo,
        Instant orderedAt,
        String buyerName,
        String buyerEmail,
        String buyerPhone,
        String paymentMethod,
        int totalPaymentAmount,
        Instant paidAt,
        List<ExternalOrderItemPayload> items) {

    public ExternalOrderPayload {
        items = items != null ? List.copyOf(items) : List.of();
    }
}
