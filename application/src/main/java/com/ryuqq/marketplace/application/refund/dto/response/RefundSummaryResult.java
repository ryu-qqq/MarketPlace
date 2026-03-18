package com.ryuqq.marketplace.application.refund.dto.response;

/** 환불 상태별 요약 결과. */
public record RefundSummaryResult(
        long requested, long collecting, long collected, long completed, long rejected, long cancelled) {}
