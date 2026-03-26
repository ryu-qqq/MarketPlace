package com.ryuqq.marketplace.application.exchange.dto.command;

import java.util.List;

/** 교환 완료 일괄 처리 명령. */
public record CompleteExchangeBatchCommand(
        List<String> exchangeClaimIds, String processedBy, Long sellerId) {}
