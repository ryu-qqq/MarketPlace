package com.ryuqq.marketplace.domain.settlement.query;

import com.ryuqq.marketplace.domain.common.vo.DateField;

/** 정산 날짜 필드. */
public enum SettlementDateField implements DateField {
    EXPECTED_SETTLEMENT("expectedSettlementDay"),
    SETTLEMENT("settlementDay"),
    ORDERED("orderedAt");

    private final String fieldName;

    SettlementDateField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static SettlementDateField defaultField() {
        return EXPECTED_SETTLEMENT;
    }
}
