package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

import java.util.List;

/**
 * 네이버 커머스 상품주문 상세 조회 응답.
 *
 * <p>POST /v1/pay-order/seller/product-orders/query 응답 래퍼.
 *
 * @param data 상품주문 상세 목록
 */
public record NaverProductOrderDetailResponse(List<NaverProductOrderDetail> data) {

    public NaverProductOrderDetailResponse {
        data = data != null ? List.copyOf(data) : List.of();
    }
}
