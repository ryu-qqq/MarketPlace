package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

import java.util.List;

/**
 * 네이버 커머스 발송 지연 처리 요청.
 *
 * <p>POST /v1/pay-order/seller/product-orders/delay 요청 본문.
 *
 * @param productOrderIds 발송 지연할 상품주문번호 목록
 * @param dispatchDueDate 발송 기한 (yyyy-MM-dd)
 * @param reason 지연 사유
 */
public record NaverOrderDelayRequest(
        List<String> productOrderIds, String dispatchDueDate, String reason) {

    public NaverOrderDelayRequest {
        productOrderIds = productOrderIds != null ? List.copyOf(productOrderIds) : List.of();
    }
}
