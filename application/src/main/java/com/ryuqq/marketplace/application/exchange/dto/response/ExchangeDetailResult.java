package com.ryuqq.marketplace.application.exchange.dto.response;

import com.ryuqq.marketplace.application.claimhistory.dto.response.ClaimHistoryResult;
import java.time.Instant;
import java.util.List;

/** 교환 상세 결과. */
public record ExchangeDetailResult(
        String exchangeClaimId,
        String claimNumber,
        String orderItemId,
        long sellerId,
        int exchangeQty,
        String exchangeStatus,
        String reasonType,
        String reasonDetail,
        ExchangeOptionResult exchangeOption,
        AmountAdjustmentResult amountAdjustment,
        CollectShipmentResult collectShipment,
        String linkedOrderId,
        String requestedBy,
        String processedBy,
        Instant requestedAt,
        Instant processedAt,
        Instant completedAt,
        Instant createdAt,
        Instant updatedAt,
        List<ClaimHistoryResult> histories) {

    public record ExchangeOptionResult(
            long originalProductId,
            String originalSkuCode,
            long targetProductGroupId,
            long targetProductId,
            String targetSkuCode,
            int quantity) {}

    public record AmountAdjustmentResult(
            int originalPrice,
            int targetPrice,
            int priceDifference,
            boolean additionalPaymentRequired,
            boolean partialRefundRequired,
            int collectShippingFee,
            int reshipShippingFee,
            int totalShippingFee,
            String shippingFeePayer) {}

    public record CollectShipmentResult(
            String collectDeliveryCompany,
            String collectTrackingNumber,
            String collectStatus) {}
}
