package com.ryuqq.marketplace.domain.seller.id;

/** 셀러 정산 ID Value Object. */
public record SellerSettlementId(Long value) {

    public static SellerSettlementId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("SellerSettlementId 값은 null일 수 없습니다");
        }
        return new SellerSettlementId(value);
    }

    public static SellerSettlementId forNew() {
        return new SellerSettlementId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
