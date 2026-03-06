package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

import java.util.List;

/**
 * 네이버 커머스 상품주문 상세 일괄 조회 요청.
 *
 * <p>POST /v1/pay-order/seller/product-orders/query 요청 본문.
 *
 * @param productOrderIds 조회할 상품주문번호 목록
 */
public record NaverProductOrderQueryRequest(List<String> productOrderIds) {

    public NaverProductOrderQueryRequest {
        productOrderIds = productOrderIds != null ? List.copyOf(productOrderIds) : List.of();
    }
}
