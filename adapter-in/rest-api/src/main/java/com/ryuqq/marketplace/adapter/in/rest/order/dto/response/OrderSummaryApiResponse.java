package com.ryuqq.marketplace.adapter.in.rest.order.dto.response;

/** 주문 상태별 요약 응답. */
public record OrderSummaryApiResponse(
        long placed, long confirmed, long shipping, long delivered, long completed) {

    public static OrderSummaryApiResponse empty() {
        return new OrderSummaryApiResponse(0, 0, 0, 0, 0);
    }
}
