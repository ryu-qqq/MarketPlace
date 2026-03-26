package com.ryuqq.marketplace.domain.claimhistory.vo;

/** 클레임 이력 액터 타입. */
public enum ActorType {
    CUSTOMER("고객"),
    SELLER("판매자"),
    ADMIN("관리자"),
    SYSTEM("시스템");

    private final String description;

    ActorType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
