package com.ryuqq.marketplace.domain.productgroup.vo;

/** 상품 그룹 상태. */
public enum ProductGroupStatus {

    DRAFT("임시저장"),
    ACTIVE("판매중"),
    INACTIVE("판매중지"),
    SOLDOUT("품절"),
    DELETED("삭제");

    private final String displayName;

    ProductGroupStatus(String displayName) {
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

    /** ACTIVE로 전환 가능한지 확인. DRAFT, INACTIVE, SOLDOUT에서만 가능. */
    public boolean canActivate() {
        return this == DRAFT || this == INACTIVE || this == SOLDOUT;
    }

    /** 삭제 가능한지 확인. 이미 삭제된 상태가 아니면 가능. */
    public boolean canDelete() {
        return this != DELETED;
    }
}
