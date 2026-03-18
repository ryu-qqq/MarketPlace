package com.ryuqq.marketplace.application.refund.dto.command;

import java.util.List;

/** 환불 수거 완료 일괄 처리 명령 (COLLECTING → COLLECTED). sellerId가 null이면 슈퍼어드민. */
public record CollectRefundBatchCommand(
        List<String> refundClaimIds, String processedBy, Long sellerId) {}
