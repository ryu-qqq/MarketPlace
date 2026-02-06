package com.ryuqq.marketplace.domain.commoncode.id;

/** 공통 코드 ID Value Object. */
public record CommonCodeId(Long value) {

    public static CommonCodeId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("CommonCodeId 값은 null일 수 없습니다");
        }
        return new CommonCodeId(value);
    }

    public static CommonCodeId forNew() {
        return new CommonCodeId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
