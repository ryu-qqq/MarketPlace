package com.ryuqq.marketplace.domain.settlement.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/** 정산 정렬 키. */
public enum SettlementSortKey implements SortKey {
    EXPECTED_SETTLEMENT_DAY("expectedSettlementDay"),
    SETTLEMENT_DAY("settlementDay"),
    CREATED_AT("createdAt");

    private final String fieldName;

    SettlementSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static SettlementSortKey defaultKey() {
        return CREATED_AT;
    }
}
