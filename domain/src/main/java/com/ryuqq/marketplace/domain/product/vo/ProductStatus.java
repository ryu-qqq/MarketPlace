package com.ryuqq.marketplace.domain.product.vo;

/** 상품(SKU) 상태. */
public enum ProductStatus {
    ACTIVE("판매중"),
    INACTIVE("판매중지"),
    SOLDOUT("품절"),
    DELETED("삭제");

    private final String displayName;

    ProductStatus(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isDeleted() {
        return this == DELETED;
    }

    /** 활성화 가능 여부. INACTIVE, SOLDOUT에서만 가능. */
    public boolean canActivate() {
        return this == INACTIVE || this == SOLDOUT;
    }

    /** 삭제 가능 여부. 이미 삭제된 상태가 아니면 가능. */
    public boolean canDelete() {
        return this != DELETED;
    }
}
