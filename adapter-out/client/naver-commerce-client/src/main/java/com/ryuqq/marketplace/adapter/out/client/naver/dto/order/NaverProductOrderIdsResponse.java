package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

import java.util.List;

/**
 * 네이버 커머스 상품주문번호 목록 응답.
 *
 * <p>GET /v1/pay-order/seller/orders/{orderId}/product-order-ids 응답 래퍼.
 *
 * @param data 응답 데이터
 */
public record NaverProductOrderIdsResponse(Data data) {

    /**
     * 응답 데이터.
     *
     * @param productOrderIds 상품주문번호 목록
     */
    public record Data(List<String> productOrderIds) {

        public Data {
            productOrderIds = productOrderIds != null ? List.copyOf(productOrderIds) : List.of();
        }
    }
}
