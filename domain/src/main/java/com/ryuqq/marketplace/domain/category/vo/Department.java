package com.ryuqq.marketplace.domain.category.vo;

import java.util.Locale;

/** 상품 부문. */
public enum Department {
    FASHION,
    BEAUTY,
    LIVING,
    FOOD,
    DIGITAL,
    SPORTS,
    KIDS,
    PET,
    CULTURE,
    HEALTH,
    ETC;

    public static Department fromString(String value) {
        if (value == null || value.isBlank()) {
            return FASHION;
        }
        try {
            return Department.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return FASHION;
        }
    }
}
