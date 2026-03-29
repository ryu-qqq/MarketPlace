package com.ryuqq.marketplace.application.legacy.order.dto.result;

import java.time.Instant;
import java.util.List;

/**
 * 레거시 주문 단건 조회 결과.
 *
 * <p>세토프 OrderResponse와 동일한 구조를 제공하기 위해 필요한 모든 필드를 포함합니다.
 */
public record LegacyOrderDetailResult(
        long orderId,
        long paymentId,
        long productId,
        long sellerId,
        long userId,
        long orderAmount,
        String orderStatus,
        int quantity,
        Instant orderDate,
        long productGroupId,
        String productGroupName,
        long brandId,
        String brandName,
        long categoryId,
        long regularPrice,
        long currentPrice,
        double commissionRate,
        double shareRatio,
        List<String> optionValues,
        String mainImageUrl,
        String receiverName,
        String receiverPhone,
        String receiverZipCode,
        String receiverAddress,
        String receiverAddressDetail,
        String deliveryRequest,
        // payment 추가 필드
        String paymentAgencyId,
        String paymentStatus,
        String paymentMethod,
        Instant paymentCanceledDate,
        String siteName,
        long billAmount,
        long usedMileageAmount,
        // buyer 추가 필드
        String buyerName,
        String buyerEmail,
        String buyerPhone,
        // 배송 추가 필드
        String deliveryStatus,
        Instant shipmentCompletedDate,
        String shipmentInvoiceNo,
        String shipmentCompanyCode,
        // product 추가 필드
        String sellerName,
        String deliveryArea,
        String skuNumber,
        String managementType,
        String optionType,
        String productCondition,
        String origin,
        String styleCode,
        long directDiscountPrice,
        int discountRate) {}
