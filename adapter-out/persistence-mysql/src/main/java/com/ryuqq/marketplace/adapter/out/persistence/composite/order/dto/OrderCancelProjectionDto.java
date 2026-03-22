package com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto;

import java.time.Instant;

/** 주문 취소 프로젝션. cancels 테이블에서 조회. */
public record OrderCancelProjectionDto(
        String cancelId,
        String orderItemId,
        String cancelNumber,
        String cancelStatus,
        int quantity,
        String reasonType,
        String reasonDetail,
        Integer refundAmount,
        String refundMethod,
        Instant refundedAt,
        Instant requestedAt,
        Instant completedAt) {}
