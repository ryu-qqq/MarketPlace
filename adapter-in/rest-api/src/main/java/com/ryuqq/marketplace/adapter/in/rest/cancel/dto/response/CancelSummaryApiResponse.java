package com.ryuqq.marketplace.adapter.in.rest.cancel.dto.response;

/** 취소 상태별 요약 응답. */
public record CancelSummaryApiResponse(
        long requested, long approved, long rejected, long completed) {

    public static CancelSummaryApiResponse empty() {
        return new CancelSummaryApiResponse(0, 0, 0, 0);
    }
}
