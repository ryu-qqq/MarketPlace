package com.ryuqq.marketplace.application.exchange.dto.command;

import java.util.List;

/** 교환 거절 일괄 처리 명령. sellerId가 null이면 슈퍼어드민. */
public record RejectExchangeBatchCommand(
        List<String> exchangeClaimIds, String processedBy, Long sellerId) {}
