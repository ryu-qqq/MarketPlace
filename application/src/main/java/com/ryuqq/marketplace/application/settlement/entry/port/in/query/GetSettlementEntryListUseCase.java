package com.ryuqq.marketplace.application.settlement.entry.port.in.query;

import com.ryuqq.marketplace.application.settlement.dto.response.SettlementEntryPageResult;
import com.ryuqq.marketplace.application.settlement.entry.dto.query.SettlementEntrySearchParams;

/** 정산 원장 목록 조회 UseCase. */
public interface GetSettlementEntryListUseCase {

    /** 검색 조건으로 정산 원장 목록을 페이징 조회합니다. */
    SettlementEntryPageResult execute(SettlementEntrySearchParams params);
}
