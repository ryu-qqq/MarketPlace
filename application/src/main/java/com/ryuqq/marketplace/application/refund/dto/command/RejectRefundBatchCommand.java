package com.ryuqq.marketplace.application.refund.dto.command;

import java.util.List;

/** 환불 거절 일괄 처리 명령. sellerId가 null이면 슈퍼어드민. */
public record RejectRefundBatchCommand(
        List<String> refundClaimIds, String processedBy, Long sellerId) {}
