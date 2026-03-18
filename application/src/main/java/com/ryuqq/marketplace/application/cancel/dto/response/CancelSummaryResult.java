package com.ryuqq.marketplace.application.cancel.dto.response;

/** 취소 상태별 요약 결과. */
public record CancelSummaryResult(
        long requested, long approved, long rejected, long completed, long cancelled) {}
