package com.ryuqq.marketplace.domain.shop.id;

/** Shop ID Value Object. */
public record ShopId(Long value) {

    public static ShopId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("ShopId 값은 null일 수 없습니다");
        }
        return new ShopId(value);
    }

    public static ShopId forNew() {
        return new ShopId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
