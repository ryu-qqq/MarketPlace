package com.ryuqq.marketplace.application.cancel.dto.response;

import com.ryuqq.marketplace.application.claimhistory.dto.response.ClaimHistoryResult;
import java.time.Instant;
import java.util.List;

/** 취소 상세 결과. */
public record CancelDetailResult(
        String cancelId,
        String cancelNumber,
        String orderItemId,
        int cancelQty,
        String cancelType,
        String cancelStatus,
        String reasonType,
        String reasonDetail,
        RefundInfo refundInfo,
        String requestedBy,
        String processedBy,
        Instant requestedAt,
        Instant processedAt,
        Instant completedAt,
        Instant createdAt,
        Instant updatedAt,
        List<ClaimHistoryResult> histories) {

    public record RefundInfo(
            int refundAmount,
            String refundMethod,
            String refundStatus,
            Instant refundedAt,
            String pgRefundId) {}
}
