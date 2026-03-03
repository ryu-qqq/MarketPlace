package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

/**
 * 네이버 커머스 변경 상품주문 상태.
 *
 * <p>last-changed-statuses API 응답의 개별 항목.
 *
 * @param productOrderId 상품주문번호
 * @param orderId 주문번호
 * @param lastChangedType 변경 유형 (PAYED, DELIVERED 등)
 * @param lastChangedDate 변경일시
 * @param receivedDate 수신일시
 */
public record NaverLastChangedStatus(
        String productOrderId,
        String orderId,
        String lastChangedType,
        String lastChangedDate,
        String receivedDate) {}
