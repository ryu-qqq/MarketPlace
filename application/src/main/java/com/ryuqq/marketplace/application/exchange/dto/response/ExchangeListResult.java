package com.ryuqq.marketplace.application.exchange.dto.response;

import java.time.Instant;

/** 교환 목록 결과. */
public record ExchangeListResult(
        String exchangeClaimId,
        String claimNumber,
        String orderItemId,
        int exchangeQty,
        String exchangeStatus,
        String reasonType,
        String reasonDetail,
        String targetSkuCode,
        Integer targetQuantity,
        String linkedOrderId,
        String requestedBy,
        String processedBy,
        Instant requestedAt,
        Instant processedAt,
        Instant completedAt) {}
