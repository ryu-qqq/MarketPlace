package com.ryuqq.marketplace.domain.claimhistory.vo;

/** 클레임 타입. Cancel/Refund/Exchange 공통으로 사용합니다. */
public enum ClaimType {
    CANCEL("취소"),
    REFUND("환불"),
    EXCHANGE("교환");

    private final String description;

    ClaimType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
