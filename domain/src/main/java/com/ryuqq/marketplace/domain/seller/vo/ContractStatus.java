package com.ryuqq.marketplace.domain.seller.vo;

/** 계약 상태 Enum. */
public enum ContractStatus {
    /** 활성 (계약 유효) */
    ACTIVE,

    /** 만료됨 */
    EXPIRED,

    /** 해지됨 */
    TERMINATED;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isExpired() {
        return this == EXPIRED;
    }

    public boolean isTerminated() {
        return this == TERMINATED;
    }
}
