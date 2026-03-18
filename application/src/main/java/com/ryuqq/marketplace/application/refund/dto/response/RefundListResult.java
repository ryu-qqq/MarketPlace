package com.ryuqq.marketplace.application.refund.dto.response;

import java.time.Instant;

/** 환불 목록 항목 결과. */
public record RefundListResult(
        String refundClaimId,
        String claimNumber,
        String orderItemId,
        int refundQty,
        String refundStatus,
        String reasonType,
        String reasonDetail,
        Integer originalAmount,
        Integer finalAmount,
        String refundMethod,
        String requestedBy,
        String processedBy,
        Instant requestedAt,
        Instant processedAt,
        Instant completedAt) {}
