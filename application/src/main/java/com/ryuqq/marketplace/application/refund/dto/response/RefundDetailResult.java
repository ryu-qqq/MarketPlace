package com.ryuqq.marketplace.application.refund.dto.response;

import com.ryuqq.marketplace.application.claimhistory.dto.response.ClaimHistoryResult;
import java.time.Instant;
import java.util.List;

/** 환불 상세 결과. */
public record RefundDetailResult(
        String refundClaimId,
        String claimNumber,
        String orderItemId,
        int refundQty,
        String refundStatus,
        String reasonType,
        String reasonDetail,
        RefundInfoResult refundInfo,
        HoldInfoResult holdInfo,
        CollectShipmentResult collectShipment,
        String requestedBy,
        String processedBy,
        Instant requestedAt,
        Instant processedAt,
        Instant completedAt,
        Instant createdAt,
        Instant updatedAt,
        List<ClaimHistoryResult> histories) {

    public record RefundInfoResult(
            int originalAmount,
            int finalAmount,
            int deductionAmount,
            String deductionReason,
            String refundMethod,
            Instant refundedAt) {}

    public record HoldInfoResult(String holdReason, Instant holdAt) {}

    public record CollectShipmentResult(
            String collectDeliveryCompany, String collectTrackingNumber, String collectStatus) {}
}
