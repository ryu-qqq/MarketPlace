package com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto;

import java.time.Instant;

/**
 * 상품주문(productOrder) 상세 프로젝션. order_items JOIN orders LEFT JOIN payments 결과. 리스트 프로젝션({@link
 * ProductOrderListProjectionDto})과 동일한 구조.
 */
public record ProductOrderDetailProjectionDto(
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
        String orderItemNumber,
        Long productGroupId,
        Long sellerId,
        Long brandId,
        Long productId,
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
        String orderItemStatus,
        String externalOrderStatus,
        // -- payments 테이블 (LEFT JOIN) --
        String paymentId,
        String paymentNumber,
        String paymentStatus,
        String paymentMethod,
        String paymentAgencyId,
        Integer paymentAmount,
        Instant paidAt,
        Instant canceledAt) {}
