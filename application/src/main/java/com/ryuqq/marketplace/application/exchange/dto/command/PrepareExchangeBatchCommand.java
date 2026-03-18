package com.ryuqq.marketplace.application.exchange.dto.command;

import java.util.List;

/** 교환 준비 완료 일괄 처리 명령 (COLLECTED → PREPARING). sellerId가 null이면 슈퍼어드민. */
public record PrepareExchangeBatchCommand(
        List<String> exchangeClaimIds, String processedBy, Long sellerId) {}
