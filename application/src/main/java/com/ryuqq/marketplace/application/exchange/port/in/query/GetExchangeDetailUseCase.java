package com.ryuqq.marketplace.application.exchange.port.in.query;

import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeDetailResult;

/** 교환 상세 조회 UseCase. */
public interface GetExchangeDetailUseCase {
    ExchangeDetailResult execute(String exchangeClaimId);
}
