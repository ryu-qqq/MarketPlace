package com.ryuqq.marketplace.application.refund.dto.command;

import java.util.List;

/** 환불 승인 일괄 처리 명령 (수거 시작). sellerId가 null이면 슈퍼어드민. */
public record ApproveRefundBatchCommand(
        List<String> refundClaimIds, String processedBy, Long sellerId) {}
