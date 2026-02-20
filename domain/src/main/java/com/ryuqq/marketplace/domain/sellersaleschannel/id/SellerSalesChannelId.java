package com.ryuqq.marketplace.domain.sellersaleschannel.id;

/** 셀러 판매채널 ID Value Object. */
public record SellerSalesChannelId(Long value) {

    public static SellerSalesChannelId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("SellerSalesChannelId 값은 null일 수 없습니다");
        }
        return new SellerSalesChannelId(value);
    }

    public static SellerSalesChannelId forNew() {
        return new SellerSalesChannelId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
