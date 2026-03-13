package com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto;

import java.time.Instant;

/** 주문 클레임 프로젝션. */
public record OrderClaimProjectionDto(
        Long claimId,
        String orderItemId,
        String claimNumber,
        String claimType,
        String claimStatus,
        int quantity,
        String reasonType,
        String reasonDetail,
        String collectMethod,
        int originalAmount,
        int deductionAmount,
        String deductionReason,
        int refundAmount,
        String refundMethod,
        Instant refundedAt,
        Instant requestedAt,
        Instant completedAt,
        Instant rejectedAt) {}
