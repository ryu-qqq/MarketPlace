package com.ryuqq.marketplace.domain.legacy.productgroup.vo;

/** 레거시(세토프) 상품 관리 유형 enum. */
public enum ManagementType {
    MENUAL("수동등록"),
    AUTO("크롤링"),
    SABANG("사방넷"),
    SEWON("세원 셀릭");

    private final String description;

    ManagementType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
