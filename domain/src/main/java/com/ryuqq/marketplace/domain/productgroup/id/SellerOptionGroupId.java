package com.ryuqq.marketplace.domain.productgroup.id;

/** 셀러 옵션 그룹 ID Value Object. */
public record SellerOptionGroupId(Long value) {

    public static SellerOptionGroupId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("SellerOptionGroupId 값은 null일 수 없습니다");
        }
        return new SellerOptionGroupId(value);
    }

    public static SellerOptionGroupId forNew() {
        return new SellerOptionGroupId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
