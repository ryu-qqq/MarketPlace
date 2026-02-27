package com.ryuqq.marketplace.adapter.in.rest.exchange.dto.response;

/** 교환 상태별 요약 응답. */
public record ExchangeSummaryApiResponse(
        long requested,
        long collecting,
        long collected,
        long shipping,
        long completed,
        long rejected,
        long cancelled) {

    public static ExchangeSummaryApiResponse empty() {
        return new ExchangeSummaryApiResponse(0, 0, 0, 0, 0, 0, 0);
    }
}
