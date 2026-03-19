package com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto;

import java.time.Instant;

/** 주문 상품 프로젝션 (order_items 전체 필드). */
public record OrderItemProjectionDto(
        String orderItemId,
        String orderId,
        long productGroupId,
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
        String orderItemStatus) {}
