package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * 네이버 커머스 발송 처리 요청.
 *
 * <p>POST /v1/pay-order/seller/product-orders/dispatch 요청 본문. 최대 30건.
 *
 * @param dispatchProductOrders 발송 처리할 상품주문 목록
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NaverOrderDispatchRequest(List<DispatchProductOrder> dispatchProductOrders) {

    public NaverOrderDispatchRequest {
        dispatchProductOrders =
                dispatchProductOrders != null ? List.copyOf(dispatchProductOrders) : List.of();
    }

    /**
     * 개별 발송 정보.
     *
     * @param productOrderId 상품주문번호
     * @param deliveryMethod 배송방법 (DELIVERY, DIRECT_DELIVERY 등)
     * @param deliveryCompanyCode 택배사 코드
     * @param trackingNumber 운송장번호
     * @param dispatchDate 배송일 (ISO 8601)
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DispatchProductOrder(
            String productOrderId,
            String deliveryMethod,
            String deliveryCompanyCode,
            String trackingNumber,
            String dispatchDate) {}
}
