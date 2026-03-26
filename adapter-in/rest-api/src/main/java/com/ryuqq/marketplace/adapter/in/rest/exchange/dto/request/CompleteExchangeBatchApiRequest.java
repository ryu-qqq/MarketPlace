package com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** 교환 완료 일괄 처리 API 요청. */
public record CompleteExchangeBatchApiRequest(@NotEmpty List<String> exchangeClaimIds) {}
