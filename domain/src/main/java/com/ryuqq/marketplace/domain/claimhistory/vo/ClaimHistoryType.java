package com.ryuqq.marketplace.domain.claimhistory.vo;

/** 클레임 이력 타입. */
public enum ClaimHistoryType {

    STATUS_CHANGE("상태 변경"),
    MANUAL("수기 메모");

    private final String description;

    ClaimHistoryType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
