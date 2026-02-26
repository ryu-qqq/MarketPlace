package com.ryuqq.marketplace.domain.productgroup.vo;

/** 상품 그룹 상태. */
public enum ProductGroupStatus {
    DRAFT("임시저장"),
    PROCESSING("검수중"),
    PENDING_REVIEW("검수대기"),
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

    public boolean isPendingReview() {
        return this == PENDING_REVIEW;
    }

    public boolean isDeleted() {
        return this == DELETED;
    }

    /** 검수 시작 가능 여부. DRAFT에서만 가능. */
    public boolean canProcess() {
        return this == DRAFT;
    }

    /**
     * ACTIVE로 전환 가능한지 확인.
     *
     * <p>ACTIVE 포함: 이미 활성화된 상품이 재검수 통과(AUTO_APPROVED) 시 멱등 전환을 허용합니다.
     */
    public boolean canActivate() {
        return this == DRAFT
                || this == PROCESSING
                || this == PENDING_REVIEW
                || this == ACTIVE
                || this == INACTIVE
                || this == SOLDOUT;
    }

    /**
     * 검수대기(PENDING_REVIEW)로 전환 가능 여부.
     *
     * <p>ACTIVE 포함: 이미 활성화된 상품이 재검수에서 HUMAN_REVIEW 판정 시 전환을 허용합니다.
     */
    public boolean canPendingReview() {
        return this == PROCESSING || this == ACTIVE;
    }

    /**
     * 반려 가능 여부.
     *
     * <p>ACTIVE 포함: 이미 활성화된 상품이 재검수에서 AUTO_REJECTED 판정 시 전환을 허용합니다.
     */
    public boolean canReject() {
        return this == PROCESSING || this == PENDING_REVIEW || this == ACTIVE;
    }

    /** 삭제 가능한지 확인. 이미 삭제된 상태가 아니면 가능. */
    public boolean canDelete() {
        return this != DELETED;
    }
}
