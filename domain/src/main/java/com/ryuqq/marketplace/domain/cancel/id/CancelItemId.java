package com.ryuqq.marketplace.domain.cancel.id;

/** 취소 상품 ID Value Object. */
public record CancelItemId(Long value) {

    public static CancelItemId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("CancelItemId 값은 null일 수 없습니다");
        }
        return new CancelItemId(value);
    }

    public static CancelItemId forNew() {
        return new CancelItemId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
