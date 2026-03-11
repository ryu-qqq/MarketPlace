package com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto;

import java.time.Instant;

/** 결제 정보 프로젝션 (payments 테이블). */
public record PaymentProjectionDto(
        String paymentId,
        String paymentNumber,
        String orderId,
        String paymentStatus,
        String paymentMethod,
        String paymentAgencyId,
        int paymentAmount,
        Instant paidAt,
        Instant canceledAt) {}
