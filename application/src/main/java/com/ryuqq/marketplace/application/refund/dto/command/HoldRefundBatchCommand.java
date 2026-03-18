package com.ryuqq.marketplace.application.refund.dto.command;

import java.util.List;

/** 환불 보류/보류 해제 일괄 처리 커맨드. */
public record HoldRefundBatchCommand(
        List<String> refundClaimIds,
        boolean isHold,
        String memo,
        String processedBy,
        Long sellerId) {

    public String operationName() {
        return isHold ? "HOLD" : "RELEASE_HOLD";
    }
}
