package com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto;

import java.time.Instant;

/**
 * 상품주문(productOrder) 목록 프로젝션. order_items JOIN orders LEFT JOIN payments 결과를 1:1 매핑한다. 각 행은 하나의
 * order_item을 나타내며, 부모 주문 정보와 결제 정보를 함께 포함한다.
 */
public record ProductOrderListProjectionDto(
        // -- orders 테이블 --
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
        Instant orderCreatedAt,
        Instant orderUpdatedAt,
        // -- order_items 테이블 --
        Long orderItemId,
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
        int itemPaymentAmount,
        String receiverName,
        String receiverPhone,
        String receiverZipcode,
        String receiverAddress,
        String receiverAddressDetail,
        String deliveryRequest,
        String deliveryStatus,
        String shipmentCompanyCode,
        String invoice,
        Instant shipmentCompletedDate,
        // -- payments 테이블 (LEFT JOIN) --
        String paymentId,
        String paymentNumber,
        String paymentStatus,
        String paymentMethod,
        String paymentAgencyId,
        Integer paymentAmount,
        Instant paidAt,
        Instant canceledAt) {}
