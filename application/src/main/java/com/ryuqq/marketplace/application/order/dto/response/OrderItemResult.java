package com.ryuqq.marketplace.application.order.dto.response;

/**
 * 주문 상품 조회 결과.
 *
 * @param orderItemId 주문 상품 ID
 * @param orderItemNumber 주문 상품 번호
 * @param orderId 주문 ID
 * @param productGroupId 내부 상품그룹 ID
 * @param productId 내부 상품 ID
 * @param skuCode SKU 코드
 * @param productGroupName 상품그룹명
 * @param brandName 브랜드명 (스냅샷)
 * @param sellerName 셀러명 (스냅샷)
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
 * @param receiverName 수령인명
 * @param receiverPhone 수령인 전화번호
 * @param receiverZipcode 수령인 우편번호
 * @param receiverAddress 수령인 주소
 * @param receiverAddressDetail 수령인 상세주소
 * @param deliveryRequest 배송 요청사항
 * @param orderItemStatus 주문 상품 상태
 * @param externalOrderStatus 외부몰 주문 상태
 */
public record OrderItemResult(
        Long orderItemId,
        String orderItemNumber,
        String orderId,
        long productGroupId,
        Long sellerId,
        Long brandId,
        Long categoryId,
        long productId,
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
        int regularPrice,
        int unitPrice,
        int quantity,
        int totalAmount,
        int discountAmount,
        int paymentAmount,
        String receiverName,
        String receiverPhone,
        String receiverZipcode,
        String receiverAddress,
        String receiverAddressDetail,
        String deliveryRequest,
        String orderItemStatus,
        String externalOrderStatus) {}
