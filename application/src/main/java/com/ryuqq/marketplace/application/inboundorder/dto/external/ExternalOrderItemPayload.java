package com.ryuqq.marketplace.application.inboundorder.dto.external;

/**
 * 외부몰 주문 아이템 데이터.
 *
 * @param externalProductOrderId 외부 상품주문 ID (예: 네이버 productOrderId)
 * @param externalProductId 외부 상품 ID
 * @param externalOptionId 외부 옵션 ID
 * @param externalProductName 외부 상품명
 * @param externalOptionName 외부 옵션명
 * @param externalImageUrl 외부 이미지 URL
 * @param unitPrice 개당 판매가
 * @param quantity 수량
 * @param totalAmount 합계 금액
 * @param discountAmount 할인 금액
 * @param sellerBurdenDiscountAmount 판매자 부담 할인액
 * @param paymentAmount 실결제 금액
 * @param receiverName 수령인명
 * @param receiverPhone 수령인 전화번호
 * @param receiverZipCode 우편번호
 * @param receiverAddress 주소
 * @param receiverAddressDetail 상세주소
 * @param deliveryRequest 배송 요청사항
 */
@SuppressWarnings("PMD.MethodNamingConventions")
@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
        value = "NM_CONFUSING",
        justification = "receiverZipCode follows domain convention")
public record ExternalOrderItemPayload(
        String externalProductOrderId,
        String externalProductId,
        String externalOptionId,
        String externalProductName,
        String externalOptionName,
        String externalImageUrl,
        int unitPrice,
        int quantity,
        int totalAmount,
        int discountAmount,
        int sellerBurdenDiscountAmount,
        int paymentAmount,
        String receiverName,
        String receiverPhone,
        String receiverZipCode,
        String receiverAddress,
        String receiverAddressDetail,
        String deliveryRequest) {}
