package com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/** 일별 정산 조회 요청 파라미터. */
public record DailySettlementApiRequest(
        @NotNull String startDate,
        @NotNull String endDate,
        List<Long> sellerIds,
        @NotNull Integer page,
        @NotNull Integer size) {}
