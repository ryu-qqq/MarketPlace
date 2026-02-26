package com.ryuqq.marketplace.domain.saleschannelbrand.id;

public record SalesChannelBrandId(Long value) {

    public static SalesChannelBrandId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("SalesChannelBrandId 값은 null일 수 없습니다");
        }
        return new SalesChannelBrandId(value);
    }

    public static SalesChannelBrandId forNew() {
        return new SalesChannelBrandId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
