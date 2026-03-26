package com.ryuqq.marketplace.application.exchange.dto.response;

/** 교환 상태별 요약 결과. */
public record ExchangeSummaryResult(
        long requested,
        long collecting,
        long collected,
        long preparing,
        long shipping,
        long completed,
        long rejected,
        long cancelled) {}
