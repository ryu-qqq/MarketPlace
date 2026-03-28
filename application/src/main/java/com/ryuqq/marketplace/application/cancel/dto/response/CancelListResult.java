package com.ryuqq.marketplace.application.cancel.dto.response;

import java.time.Instant;

/** 취소 목록 항목 결과. */
public record CancelListResult(
        String cancelId,
        String cancelNumber,
        Long orderItemId,
        int cancelQty,
        String cancelType,
        String cancelStatus,
        String reasonType,
        String reasonDetail,
        Integer refundAmount,
        String refundMethod,
        String requestedBy,
        String processedBy,
        Instant requestedAt,
        Instant processedAt,
        Instant completedAt) {}
