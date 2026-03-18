package com.ryuqq.marketplace.application.exchange.dto.command;

import java.util.List;

/** 교환 수거 완료 일괄 처리 명령 (COLLECTING → COLLECTED). sellerId가 null이면 슈퍼어드민. */
public record CollectExchangeBatchCommand(
        List<String> exchangeClaimIds, String processedBy, Long sellerId) {}
