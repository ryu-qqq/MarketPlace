package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

/**
 * 네이버 커머스 상품주문 상세.
 *
 * <p>product-orders/query API 응답의 개별 항목. 주문·상품주문·배송 정보를 포함.
 *
 * @param order 주문 레벨 정보
 * @param productOrder 상품주문 레벨 정보
 * @param delivery 배송 정보
 */
public record NaverProductOrderDetail(
        NaverProductOrderOrder order, ProductOrderInfo productOrder, NaverDeliveryInfo delivery) {

    /**
     * 상품주문 레벨 정보.
     *
     * @param productOrderId 상품주문번호
     * @param productOrderStatus 상품주문 상태
     * @param productId 상품번호
     * @param productName 상품명
     * @param productOption 옵션명
     * @param optionCode 옵션 코드
     * @param sellerProductCode 판매자 상품코드
     * @param optionManageCode 옵션 관리코드
     * @param quantity 수량
     * @param unitPrice 개당 판매가
     * @param totalProductAmount 총 상품금액
     * @param productDiscountAmount 상품 할인금액
     * @param totalPaymentAmount 총 결제금액
     * @param shippingAddress 배송지 정보
     * @param shippingMemo 배송 요청사항
     */
    public record ProductOrderInfo(
            String productOrderId,
            String productOrderStatus,
            String productId,
            String productName,
            String productOption,
            String optionCode,
            String sellerProductCode,
            String optionManageCode,
            int quantity,
            int unitPrice,
            int totalProductAmount,
            int productDiscountAmount,
            int totalPaymentAmount,
            NaverShippingAddress shippingAddress,
            String shippingMemo) {}
}
