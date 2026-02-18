package com.ryuqq.marketplace.domain.productgroup.vo;

/** 상품 그룹 상태. */
public enum ProductGroupStatus {
    DRAFT("임시저장"),
    PROCESSING("검수중"),
    ACTIVE("판매중"),
    INACTIVE("판매중지"),
    SOLDOUT("품절"),
    REJECTED("반려"),
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

    public boolean isProcessing() {
        return this == PROCESSING;
    }

    public boolean isRejected() {
        return this == REJECTED;
    }

    public boolean isDeleted() {
        return this == DELETED;
    }

    /** 검수 시작 가능 여부. DRAFT에서만 가능. */
    public boolean canProcess() {
        return this == DRAFT;
    }

    /** ACTIVE로 전환 가능한지 확인. DRAFT, PROCESSING, INACTIVE, SOLDOUT에서 가능. */
    public boolean canActivate() {
        return this == DRAFT || this == PROCESSING || this == INACTIVE || this == SOLDOUT;
    }

    /** 반려 가능 여부. PROCESSING에서만 가능. */
    public boolean canReject() {
        return this == PROCESSING;
    }

    /** 삭제 가능한지 확인. 이미 삭제된 상태가 아니면 가능. */
    public boolean canDelete() {
        return this != DELETED;
    }
}
