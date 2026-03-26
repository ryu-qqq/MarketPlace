package com.ryuqq.marketplace.application.exchange.port.in.query;

import com.ryuqq.marketplace.application.exchange.dto.query.ExchangeSearchParams;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangePageResult;

/** 교환 목록 조회 UseCase. */
public interface GetExchangeListUseCase {
    ExchangePageResult execute(ExchangeSearchParams params);
}
