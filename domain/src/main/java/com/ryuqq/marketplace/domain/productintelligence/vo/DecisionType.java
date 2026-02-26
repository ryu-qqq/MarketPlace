package com.ryuqq.marketplace.domain.productintelligence.vo;

/** 검수 최종 판정 유형. */
public enum DecisionType {

    /** 자동 승인. confidence가 높아 즉시 ACTIVE 전환. */
    AUTO_APPROVED("자동승인"),

    /** 사람 검수 필요. confidence가 중간이거나 판단 불가. */
    HUMAN_REVIEW("검수자확인"),

    /** 자동 반려. 필수 조건 미충족 + 보강 불가. */
    AUTO_REJECTED("자동반려");

    private final String description;

    DecisionType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    public boolean isApproved() {
        return this == AUTO_APPROVED;
    }

    public boolean needsReview() {
        return this == HUMAN_REVIEW;
    }

    public boolean isRejected() {
        return this == AUTO_REJECTED;
    }
}
