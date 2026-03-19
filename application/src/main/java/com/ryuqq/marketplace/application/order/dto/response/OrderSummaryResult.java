package com.ryuqq.marketplace.application.order.dto.response;

/**
 * 주문상품 상태별 요약 결과.
 *
 * @param ready 결제완료/배송준비 수량
 * @param confirmed 구매확정 수량
 * @param cancelled 취소 수량
 * @param returnRequested 반품요청 수량
 * @param returned 반품완료 수량
 */
public record OrderSummaryResult(
        long ready,
        long confirmed,
        long cancelled,
        long returnRequested,
        long returned) {}
