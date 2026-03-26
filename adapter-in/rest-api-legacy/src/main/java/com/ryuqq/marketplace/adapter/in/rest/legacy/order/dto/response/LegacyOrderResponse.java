package com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response;

import java.util.List;

/**
 * 세토프 OrderResponse 호환 응답 DTO. 세토프 레거시 어드민과 동일한 flat 구조.
 *
 * @param orderId 주문 ID
 * @param buyerInfo 구매자 정보
 * @param payment 결제 정보
 * @param receiverInfo 수령인 정보
 * @param paymentShipmentInfo 배송 정보
 * @param settlementInfo 정산 정보
 * @param orderProduct 주문 상품 정보
 * @param orderHistories 주문 이력 목록
 */
public record LegacyOrderResponse(
        long orderId,
        BuyerInfo buyerInfo,
        PaymentInfo payment,
        ReceiverInfo receiverInfo,
        PaymentShipmentInfo paymentShipmentInfo,
        SettlementInfo settlementInfo,
        OrderProductInfo orderProduct,
        List<OrderHistoryInfo> orderHistories) {

    public record BuyerInfo(String buyerName, String buyerEmail, String buyerPhoneNumber) {}

    public record PaymentInfo(
            long paymentId,
            String paymentAgencyId,
            String paymentStatus,
            String paymentMethod,
            String paymentDate,
            String canceledDate,
            long userId,
            String siteName,
            long billAmount,
            long paymentAmount,
            long usedMileageAmount) {}

    public record ReceiverInfo(
            String receiverName,
            String receiverPhoneNumber,
            String addressLine1,
            String addressLine2,
            String zipCode,
            String country,
            String deliveryRequest) {}

    public record PaymentShipmentInfo(
            String deliveryStatus,
            String shipmentCompanyCode,
            String invoice,
            String shipmentCompletedDate) {}

    public record SettlementInfo(
            double commissionRate,
            double fee,
            long expectationSettlementAmount,
            long settlementAmount,
            double shareRatio,
            String expectedSettlementDay,
            String settlementDay) {}

    public record OrderProductInfo(
            long orderId,
            ProductGroupDetails productGroupDetails,
            BrandInfo brand,
            long productGroupId,
            long productId,
            String sellerName,
            String productGroupMainImageUrl,
            String deliveryArea,
            int productQuantity,
            String orderStatus,
            long regularPrice,
            long orderAmount,
            long totalExpectedRefundMileageAmount,
            String option,
            String skuNumber,
            List<OptionInfo> options) {}

    public record ProductGroupDetails(
            String productGroupName,
            String optionType,
            String managementType,
            PriceInfo price,
            ProductStatusInfo productStatus,
            ClothesDetailInfo clothesDetailInfo,
            long sellerId,
            long categoryId,
            long brandId) {}

    public record PriceInfo(long regularPrice, long currentPrice, long salePrice) {}

    public record ProductStatusInfo(String soldOutYn, String displayYn) {}

    public record ClothesDetailInfo(String productCondition, String origin, String styleCode) {}

    public record BrandInfo(long brandId, String brandName) {}

    public record OptionInfo(
            long optionGroupId, long optionDetailId, String optionName, String optionValue) {}

    public record OrderHistoryInfo(
            long orderId,
            String changeReason,
            String changeDetailReason,
            String orderStatus,
            String invoiceNo,
            String shipmentCompanyCode,
            String updateDate) {}
}
