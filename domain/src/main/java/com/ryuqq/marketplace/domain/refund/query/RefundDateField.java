package com.ryuqq.marketplace.domain.refund.query;

import com.ryuqq.marketplace.domain.common.vo.DateField;

/** 환불 날짜 필드. */
public enum RefundDateField implements DateField {
    REQUESTED("requestedAt"),
    COMPLETED("completedAt");

    private final String fieldName;

    RefundDateField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static RefundDateField defaultField() {
        return REQUESTED;
    }
}
