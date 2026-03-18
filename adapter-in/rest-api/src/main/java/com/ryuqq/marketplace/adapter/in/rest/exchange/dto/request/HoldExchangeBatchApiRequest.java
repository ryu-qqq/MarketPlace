package com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** 교환 보류/보류 해제 일괄 처리 요청 DTO. */
public record HoldExchangeBatchApiRequest(
        @NotEmpty List<String> exchangeClaimIds,
        boolean isHold,
        String memo) {}
