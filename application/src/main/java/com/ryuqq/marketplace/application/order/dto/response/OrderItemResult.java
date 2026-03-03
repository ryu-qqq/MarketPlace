package com.ryuqq.marketplace.application.order.dto.response;

/**
 * 주문 상품 조회 결과.
 *
 * @param orderItemId 주문 상품 ID
 * @param productGroupId 내부 상품그룹 ID
 * @param productId 내부 상품 ID
 * @param skuCode SKU 코드
 * @param externalProductId 외부 상품 ID
 * @param externalProductName 외부 상품명
 * @param externalOptionName 외부 옵션명
 * @param externalImageUrl 외부 이미지 URL
 * @param unitPrice 개당 판매가
 * @param quantity 수량
 * @param paymentAmount 실결제 금액
 * @param receiverName 수령인명
 */
public record OrderItemResult(
        long orderItemId,
        long productGroupId,
        long productId,
        String skuCode,
        String externalProductId,
        String externalProductName,
        String externalOptionName,
        String externalImageUrl,
        int unitPrice,
        int quantity,
        int paymentAmount,
        String receiverName) {}
