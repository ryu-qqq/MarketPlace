package com.ryuqq.marketplace.application.shipment.dto.response;

import java.time.Instant;

/**
 * 배송 목록 조회 결과 (V4).
 *
 * <p>배송 정보를 중심으로 주문/상품주문/수령인 정보를 nested block으로 구성합니다.
 */
public record ShipmentListResult(
        ShipmentInfo shipment,
        OrderInfo order,
        ProductOrderInfo productOrder,
        ReceiverInfo receiver) {

    /**
     * 배송 정보.
     *
     * @param shipmentId 배송 ID
     * @param shipmentNumber 배송번호
     * @param status 배송 상태
     * @param trackingNumber 송장번호
     * @param courierCode 택배사 코드
     * @param courierName 택배사명
     * @param orderConfirmedAt 발주확인일시
     * @param shippedAt 발송일시
     * @param deliveredAt 배송완료일시
     * @param createdAt 등록일시
     */
    public record ShipmentInfo(
            String shipmentId,
            String shipmentNumber,
            String status,
            String trackingNumber,
            String courierCode,
            String courierName,
            Instant orderConfirmedAt,
            Instant shippedAt,
            Instant deliveredAt,
            Instant createdAt) {}

    /**
     * 주문 기본 정보.
     *
     * @param orderId 주문 ID
     * @param orderNumber 주문번호
     * @param status 주문 상태
     * @param salesChannelId 판매채널 ID
     * @param shopId 샵 ID
     * @param shopCode 샵 코드
     * @param shopName 샵 이름
     * @param externalOrderNo 외부 주문번호
     * @param externalOrderedAt 외부 주문일시
     * @param buyerName 구매자명
     * @param buyerEmail 구매자 이메일
     * @param buyerPhone 구매자 전화번호
     * @param createdAt 생성일시
     * @param updatedAt 수정일시
     */
    public record OrderInfo(
            String orderId,
            String orderNumber,
            String status,
            long salesChannelId,
            long shopId,
            String shopCode,
            String shopName,
            String externalOrderNo,
            Instant externalOrderedAt,
            String buyerName,
            String buyerEmail,
            String buyerPhone,
            Instant createdAt,
            Instant updatedAt) {}

    /**
     * 상품주문 정보.
     *
     * @param orderItemId 주문 상품 ID
     * @param productGroupId 내부 상품그룹 ID
     * @param productId 내부 상품 ID
     * @param sellerId 셀러 ID
     * @param brandId 브랜드 ID
     * @param skuCode SKU 코드
     * @param productGroupName 상품그룹명
     * @param brandName 브랜드명
     * @param sellerName 셀러명
     * @param mainImageUrl 대표 이미지 URL
     * @param externalProductId 외부 상품 ID
     * @param externalOptionId 외부 옵션 ID
     * @param externalProductName 외부 상품명
     * @param externalOptionName 외부 옵션명
     * @param externalImageUrl 외부 이미지 URL
     * @param unitPrice 개당 판매가
     * @param quantity 수량
     * @param totalAmount 총 금액
     * @param discountAmount 할인 금액
     * @param paymentAmount 실결제 금액
     */
    public record ProductOrderInfo(
            String orderItemId,
            long productGroupId,
            long productId,
            long sellerId,
            long brandId,
            String skuCode,
            String productGroupName,
            String brandName,
            String sellerName,
            String mainImageUrl,
            String externalProductId,
            String externalOptionId,
            String externalProductName,
            String externalOptionName,
            String externalImageUrl,
            int unitPrice,
            int quantity,
            int totalAmount,
            int discountAmount,
            int paymentAmount) {}

    /**
     * 수령인 정보.
     *
     * @param receiverName 수령인명
     * @param receiverPhone 수령인 전화번호
     * @param receiverZipcode 수령인 우편번호
     * @param receiverAddress 수령인 주소
     * @param receiverAddressDetail 수령인 상세주소
     * @param deliveryRequest 배송 요청사항
     */
    public record ReceiverInfo(
            String receiverName,
            String receiverPhone,
            String receiverZipcode,
            String receiverAddress,
            String receiverAddressDetail,
            String deliveryRequest) {}
}
