package com.ryuqq.marketplace.domain.selleraddress.vo;

/** 주소 유형 Enum. */
public enum AddressType {
    SHIPPING("출고지"),
    RETURN("반품지");

    private final String displayName;

    AddressType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
