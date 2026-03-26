package com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto;

import java.time.Instant;

/** 주문 클레임 프로젝션. refund_claims + exchange_claims 테이블에서 조회. */
public record OrderClaimProjectionDto(
        String claimId,
        String orderItemId,
        String claimNumber,
        String claimType,
        String claimStatus,
        int quantity,
        String reasonType,
        String reasonDetail,
        String collectMethod,
        Integer originalAmount,
        Integer deductionAmount,
        String deductionReason,
        Integer refundAmount,
        String refundMethod,
        Instant refundedAt,
        Instant requestedAt,
        Instant completedAt,
        Instant rejectedAt) {}
