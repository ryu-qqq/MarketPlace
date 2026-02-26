package com.ryuqq.marketplace.domain.saleschannelcategory.id;

public record SalesChannelCategoryId(Long value) {

    public static SalesChannelCategoryId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("SalesChannelCategoryId 값은 null일 수 없습니다");
        }
        return new SalesChannelCategoryId(value);
    }

    public static SalesChannelCategoryId forNew() {
        return new SalesChannelCategoryId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
