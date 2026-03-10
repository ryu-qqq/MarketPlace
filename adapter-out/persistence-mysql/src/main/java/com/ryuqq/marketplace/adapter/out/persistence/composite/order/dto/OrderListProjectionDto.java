package com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto;

import java.time.Instant;

/** 주문 목록 프로젝션 (orders + payments LEFT JOIN + item count subquery). */
public record OrderListProjectionDto(
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
        Instant updatedAt,
        String paymentId,
        String paymentNumber,
        String paymentStatus,
        String paymentMethod,
        int paymentAmount,
        Instant paidAt,
        long itemCount) {}
