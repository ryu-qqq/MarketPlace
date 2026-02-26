package com.ryuqq.marketplace.domain.seller.id;

/** 셀러 ID Value Object. */
public record SellerId(Long value) {

    public static SellerId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("SellerId 값은 null일 수 없습니다");
        }
        return new SellerId(value);
    }

    public static SellerId forNew() {
        return new SellerId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
