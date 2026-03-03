package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

/**
 * 네이버 커머스 배송 희망일 변경 요청.
 *
 * <p>POST /v1/pay-order/seller/product-orders/wished-delivery-date 요청 본문.
 *
 * @param productOrderId 상품주문번호
 * @param wishedDeliveryDate 희망 배송일 (yyyy-MM-dd)
 */
public record NaverWishedDeliveryDateRequest(String productOrderId, String wishedDeliveryDate) {}
