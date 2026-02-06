package com.ryuqq.marketplace.domain.category.id;

/** 카테고리 ID Value Object. */
public record CategoryId(Long value) {

    public static CategoryId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("CategoryId 값은 null일 수 없습니다");
        }
        return new CategoryId(value);
    }

    public static CategoryId forNew() {
        return new CategoryId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
