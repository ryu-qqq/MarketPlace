package com.ryuqq.marketplace.domain.order.vo;

/** 결제 상태. */
public enum PaymentStatus {
    PENDING,
    COMPLETED,
    PARTIALLY_REFUNDED,
    FULLY_REFUNDED,
    CANCELLED
}
