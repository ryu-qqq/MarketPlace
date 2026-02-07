package com.ryuqq.marketplace.domain.shippingpolicy.vo;

/** 배송비 유형 Enum. */
public enum ShippingFeeType {
    FREE("무료배송"),
    PAID("유료배송"),
    CONDITIONAL_FREE("조건부 무료배송"),
    QUANTITY_BASED("수량별 배송비");

    private final String displayName;

    ShippingFeeType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public boolean isFree() {
        return this == FREE;
    }

    public boolean isPaid() {
        return this == PAID;
    }

    public boolean isConditionalFree() {
        return this == CONDITIONAL_FREE;
    }

    public boolean isQuantityBased() {
        return this == QUANTITY_BASED;
    }
}
