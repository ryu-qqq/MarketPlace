package com.ryuqq.marketplace.domain.sellerapplication.vo;

/** 입점 신청 상태 Enum. */
public enum ApplicationStatus {
    /** 대기 중 (심사 전) */
    PENDING,

    /** 승인됨 */
    APPROVED,

    /** 거절됨 */
    REJECTED;

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isApproved() {
        return this == APPROVED;
    }

    public boolean isRejected() {
        return this == REJECTED;
    }

    public boolean isProcessed() {
        return this == APPROVED || this == REJECTED;
    }
}
