package com.ryuqq.marketplace.application.exchange.port.in.query;

import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeSummaryResult;

/** 교환 상태별 요약 조회 UseCase. */
public interface GetExchangeSummaryUseCase {
    ExchangeSummaryResult execute();
}
