package com.ryuqq.marketplace.domain.commoncodetype.id;

public record CommonCodeTypeId(Long value) {

    public static CommonCodeTypeId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("CommonCodeTypeId 값은 null일 수 없습니다");
        }
        return new CommonCodeTypeId(value);
    }

    public static CommonCodeTypeId forNew() {
        return new CommonCodeTypeId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
