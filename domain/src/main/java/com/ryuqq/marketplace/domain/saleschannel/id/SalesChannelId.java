package com.ryuqq.marketplace.domain.saleschannel.id;

/** SalesChannel ID Value Object. */
public record SalesChannelId(Long value) {

    public static SalesChannelId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("SalesChannelId 값은 null일 수 없습니다");
        }
        return new SalesChannelId(value);
    }

    public static SalesChannelId forNew() {
        return new SalesChannelId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
