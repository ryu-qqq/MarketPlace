package com.ryuqq.marketplace.application.exchange.dto.command;

import java.util.List;

/** 교환 건 환불 전환 일괄 처리 명령. */
public record ConvertToRefundBatchCommand(
        List<String> exchangeClaimIds, String processedBy, Long sellerId) {}
