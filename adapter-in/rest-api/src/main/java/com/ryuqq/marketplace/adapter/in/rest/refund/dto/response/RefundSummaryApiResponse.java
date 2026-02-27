package com.ryuqq.marketplace.adapter.in.rest.refund.dto.response;

/** 환불 상태별 요약 응답. */
public record RefundSummaryApiResponse(
        long requested,
        long collecting,
        long collected,
        long completed,
        long rejected,
        long cancelled) {

    public static RefundSummaryApiResponse empty() {
        return new RefundSummaryApiResponse(0, 0, 0, 0, 0, 0);
    }
}
