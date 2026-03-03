package com.ryuqq.marketplace.application.order.dto.command;

/**
 * 주문 상품 생성 Command.
 *
 * @param productGroupId 내부 상품그룹 ID
 * @param productId 내부 상품 ID
 * @param sellerId 셀러 ID
 * @param brandId 브랜드 ID
 * @param skuCode SKU 코드
 * @param externalProductId 외부 상품 ID
 * @param externalOptionId 외부 옵션 ID
 * @param externalProductName 외부 상품명
 * @param externalOptionName 외부 옵션명
 * @param externalImageUrl 외부 이미지 URL
 * @param unitPrice 개당 판매가
 * @param quantity 수량
 * @param totalAmount 합계 금액
 * @param discountAmount 할인 금액
 * @param paymentAmount 실결제 금액
 * @param receiverName 수령인명
 * @param receiverPhone 수령인 전화번호
 * @param receiverZipCode 우편번호
 * @param receiverAddress 주소
 * @param receiverAddressDetail 상세주소
 * @param deliveryRequest 배송 요청사항
 */
public record CreateOrderItemCommand(
        long productGroupId,
        long productId,
        long sellerId,
        long brandId,
        String skuCode,
        String externalProductId,
        String externalOptionId,
        String externalProductName,
        String externalOptionName,
        String externalImageUrl,
        int unitPrice,
        int quantity,
        int totalAmount,
        int discountAmount,
        int paymentAmount,
        String receiverName,
        String receiverPhone,
        String receiverZipCode,
        String receiverAddress,
        String receiverAddressDetail,
        String deliveryRequest) {}
