package com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response;

import java.util.List;

/** 일별 정산 목록 응답. */
public record DailySettlementApiResponse(List<Object> content, long totalElements, int totalPages) {

    public static DailySettlementApiResponse empty() {
        return new DailySettlementApiResponse(List.of(), 0, 0);
    }
}
