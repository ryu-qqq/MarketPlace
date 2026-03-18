package com.ryuqq.marketplace.application.exchange.dto.command;

import java.util.List;

/** 교환 보류/보류 해제 일괄 처리 커맨드. */
public record HoldExchangeBatchCommand(
        List<String> exchangeClaimIds,
        boolean isHold,
        String memo,
        String processedBy,
        Long sellerId) {

    public String operationName() {
        return isHold ? "HOLD" : "RELEASE_HOLD";
    }
}
