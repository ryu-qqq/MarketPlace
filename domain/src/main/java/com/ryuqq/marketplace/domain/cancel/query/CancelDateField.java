package com.ryuqq.marketplace.domain.cancel.query;

import com.ryuqq.marketplace.domain.common.vo.DateField;

/** 취소 날짜 필드. */
public enum CancelDateField implements DateField {
    REQUESTED("requestedAt"),
    COMPLETED("completedAt");

    private final String fieldName;

    CancelDateField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static CancelDateField defaultField() {
        return REQUESTED;
    }

    public static CancelDateField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return valueOf(value);
    }
}
