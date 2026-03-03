package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

import java.util.List;

/**
 * 네이버 커머스 발주 확인 요청.
 *
 * <p>POST /v1/pay-order/seller/product-orders/confirm 요청 본문. 최대 30건.
 *
 * @param productOrderIds 발주 확인할 상품주문번호 목록
 */
public record NaverOrderConfirmRequest(List<String> productOrderIds) {

    public NaverOrderConfirmRequest {
        productOrderIds = productOrderIds != null ? List.copyOf(productOrderIds) : List.of();
    }
}
